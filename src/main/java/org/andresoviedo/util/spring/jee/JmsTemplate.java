package org.andresoviedo.util.spring.jee;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate102;
import org.springframework.jms.core.SessionCallback;
import org.springframework.jms.support.JmsUtils;

/**
 * Template JMS que extiende la funcionalidad de Spring:
 * <ul>
 * <li>receiveSelected sobrecargado para especificar el timeout en cada recepción</li>
 * </ul>
 * 
 * @author andresoviedo
 */
public class JmsTemplate extends JmsTemplate102 {

	public Message receiveSelected(final String destinationName, final String messageSelector, final long timeout) throws JmsException {
		return (Message) execute(new SessionCallback() {
			public Object doInJms(Session session) throws JMSException {
				Destination destination = resolveDestinationName(session, destinationName);
				return doReceive(session, createConsumer(session, destination, messageSelector), timeout);
			}
		}, true);
	}

	/**
	 * Actually receive a JMS message.
	 * 
	 * @param session
	 *            the JMS Session to operate on
	 * @param consumer
	 *            the JMS MessageConsumer to send with
	 * @return the JMS Message received, or <code>null</code> if none
	 * @throws JMSException
	 *             if thrown by JMS API methods
	 */
	protected Message doReceive(Session session, MessageConsumer consumer, long timeout) throws JMSException {
		try {
			Message message = null;
			if (timeout == RECEIVE_TIMEOUT_NO_WAIT) {
				message = consumer.receiveNoWait();
			} else if (timeout > 0) {
				message = consumer.receive(timeout);
			} else {
				message = consumer.receive();
			}

			if (session.getTransacted()) {
				// Commit necessary - but avoid commit call within a JTA transaction.
				if (isSessionLocallyTransacted(session)) {
					// Transacted session created by this template -> commit.
					JmsUtils.commitIfNecessary(session);
				}
			} else if (isClientAcknowledge(session)) {
				// Manually acknowledge message, if any.
				if (message != null) {
					message.acknowledge();
				}
			}
			return message;
		} finally {
			JmsUtils.closeMessageConsumer(consumer);
		}
	}
}