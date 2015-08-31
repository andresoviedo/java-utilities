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

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.PhysicalAddress;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.locking.LockService;
import org.jgroups.protocols.CENTRAL_LOCK;
import org.jgroups.protocols.TCP;
import org.jgroups.protocols.TCPPING;
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

	private enum Action {
		Reboot_periodically
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
			ch = new JChannel(false);
			ProtocolStack stack = new ProtocolStack();
			ch.setProtocolStack(stack);

			List<PhysicalAddress> initial_hosts = new ArrayList<PhysicalAddress>();
			for (InetSocketAddress address : cluster_members) {
				initial_hosts.add(new IpAddress(address));
			}
			TCPPING tcpping = new TCPPING();
			tcpping.setTimeout(5000);
			tcpping.setInitialHosts(initial_hosts);

			VERIFY_SUSPECT verify_SUSPECT = new VERIFY_SUSPECT();

			TCP tcp = new TCP();
			tcp.setBindAddress(bind_address.getAddress());
			tcp.setBindPort(bind_address.getPort());

			// @formatter:off
			stack.addProtocol(tcp)
			.addProtocol(tcpping)
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
					// System.out.println("view: " + new_view);
				}

				public void receive(Message msg) {
					Address sender = msg.getSrc();
					System.out.println(msg.getObject() + " [" + sender + "]");
					if ("reboot".equals(msg.getObject())) {
						System.exit(0);
					}
				}
			});

			ch.connect("my_cluster");

			timer = new Timer();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	// reboot periodically in an ordered manner to have still availability
	public void reboot_peridically_ordered(final Calendar when) {

		// we restart 1 / day
		Date now = new Date();

		// is not time to restart yet?
		if (when.before(now)) {
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					reboot_peridically_ordered(when);
				}
			}, when.getTimeInMillis() - now.getTime());
			return;
		}

		// Get access to distributed lock service for this Reboot_periodically action...
		LockService lock_service = new LockService(ch);
		Lock lock = lock_service.getLock(Action.Reboot_periodically.name());

		// Lets try to coordinate restart...
		try {

			// Couldn't lock? maybe another member won the race to be the coordinator.
			if (!lock.tryLock(10000, TimeUnit.MILLISECONDS)) {

				// Let's recheck in 30 minutes if that another member actually did it's job
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						reboot_peridically_ordered(when);
					}
				}, TimeUnit.MINUTES.toMillis(30));
			}

			// Ok, we are the coordinator. Lets restart cluster!
			lock.unlock();
		} catch (Exception e) {
		} finally {

		}

	}

	public void aaa() throws Exception {
		// Scanner scanner = new Scanner(System.in);
		// for (int i = 0; i < 10; i++) {
		// ch.send(null, scanner.next());
		// }
		// scanner.close();

		for (int i = 0; i < 5; i++) {
			Thread.sleep((long) (1000d * Math.random()));
			LockService lock_service = new LockService(ch);
			Lock lock = lock_service.getLock("mylock");
			if (lock.tryLock(3000, TimeUnit.MILLISECONDS)) {
				try {
					Thread.sleep(1000);
					ch.send(null,
							"" + System.currentTimeMillis() + " - "
									+ String.valueOf("Lock acquired by: " + bind_address.getPort()));
					Thread.sleep(1000);
				} finally {
					lock.unlock();
				}
			}
		}

		ch.close();
	}

}