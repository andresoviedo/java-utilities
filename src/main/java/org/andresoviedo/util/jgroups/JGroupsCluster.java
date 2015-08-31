package org.andresoviedo.util.jgroups;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.PhysicalAddress;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.locking.LockService;
import org.jgroups.protocols.CENTRAL_LOCK;
import org.jgroups.protocols.TCPPING;
import org.jgroups.protocols.UDP;
import org.jgroups.protocols.UFC;
import org.jgroups.protocols.UNICAST3;
import org.jgroups.protocols.VERIFY_SUSPECT;
import org.jgroups.protocols.pbcast.FLUSH;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.protocols.pbcast.STABLE;
import org.jgroups.stack.IpAddress;
import org.jgroups.stack.ProtocolStack;

/**
 * This class represents a jgroups cluster client
 * 
 * @author andres
 * 
 */
public final class JGroupsCluster {

	private static final Logger LOG = Logger.getLogger(JGroupsCluster.class);

	private enum Action {
		Reboot
	}

	private final InetSocketAddress bind_address;
	private final List<InetSocketAddress> cluster_members;

	private JChannel ch;
	private Date start_time;
	private Timer timer;

	public JGroupsCluster(InetSocketAddress bind_address, List<InetSocketAddress> cluster_members) {
		this.bind_address = bind_address;
		this.cluster_members = cluster_members;
	}

	public void init() {
		try {
			LOG.debug("Initializing JGroupsCluster...");

			ch = new JChannel(false);
			ProtocolStack stack = new ProtocolStack();
			ch.setProtocolStack(stack);

			List<PhysicalAddress> initial_hosts = new ArrayList<PhysicalAddress>();
			for (InetSocketAddress address : cluster_members) {
				initial_hosts.add(new IpAddress(address));
			}
			TCPPING ping = new TCPPING();
			ping.setTimeout(5000);
			ping.setInitialHosts(initial_hosts);

			VERIFY_SUSPECT verify_SUSPECT = new VERIFY_SUSPECT();

			UDP udp = new UDP();
			// UDP udp = new UnicastUDP();
			udp.setBindAddress(bind_address.getAddress());
			udp.setBindPort(bind_address.getPort());
			udp.setValue("ip_mcast", false);

			// @formatter:off
			stack.addProtocol(udp)
			.addProtocol(ping)
			.addProtocol(verify_SUSPECT)
			.addProtocol(new CENTRAL_LOCK())
			.addProtocol(new NAKACK2())
			.addProtocol(new UNICAST3())
			.addProtocol(new STABLE())
			.addProtocol(new GMS())
			.addProtocol(new UFC())
			.addProtocol(new FLUSH())
			;
			// @formatter:on
			stack.init(); // (4)

			// save start time so we can later implement actions based on this date
			start_time = new Date();

			ch.setReceiver(new ReceiverAdapter() {
				public void viewAccepted(View new_view) {
					LOG.debug("Member joined cluster '" + new_view + "'");
				}

				public void receive(Message msg) {
					if (Action.Reboot.equals(msg.getObject())) {
						LOG.info("Received reboot from '" + msg.getSrc() + "'");
						// System.exit(0);
						return;
					}

					LOG.debug("Received message from '" + msg.getSrc() + "' '" + msg.getObject() + "'");
				}
			});

			ch.connect("my_cluster");

			timer = new Timer();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public void close() {
		ch.close();
	}

	public void broadcast(String message) {
		try {
			ch.send(null, message);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Reboot periodically (each day) in an ordered manner to have availability.
	 * 
	 * @param when
	 *            the future first time to reboot
	 */
	public void reboot_periodically(final Calendar when) {

		// we restart 1 / day
		Date now = new Date();

		// is not time to restart yet?
		long delay = when.getTimeInMillis() - now.getTime();
		if (delay > 0) {
			LOG.info("Executing reboot in '" + delay + "' millis...");
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					reboot_periodically(when);
				}
			}, delay);
			return;
		}

		// Get access to distributed lock service for this Reboot_periodically action...
		LockService lock_service = new LockService(ch);
		Lock lock = lock_service.getLock(Action.Reboot.name());

		// Lets try to coordinate restart...
		try {

			// Couldn't lock? maybe another member won the race to be the coordinator.
			if (!lock.tryLock(10000, TimeUnit.MILLISECONDS)) {

				// Let's recheck in 30 minutes if that another member actually did it's job
				LOG.info("I'm not the Master node. Anyway I'am will do a sanity check in 30 minutes...");
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						LOG.debug("Executing sanity check for last reboot....");
						reboot_periodically(when);
					}
				}, TimeUnit.MINUTES.toMillis(30));
				return;
			}

			// Ok, we are the coordinator. Lets restart cluster!
			lock.unlock();

			// I dont wanna be notified. I already know
			List<Address> listenersOrdered = new ArrayList<Address>(ch.getView().getMembers());
			listenersOrdered.remove(JGroupsCluster.this.bind_address);

			// Lets notify the others
			for (Address address : listenersOrdered) {
				try {
					ch.send(address, Action.Reboot);

					ch.getState(address, 3000);

					// check he is alive again?
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {

		}

	}

}