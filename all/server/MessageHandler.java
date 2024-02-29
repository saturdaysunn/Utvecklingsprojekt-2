package all.server;

import all.jointEntity.Message;

import java.util.LinkedList;

public class MessageHandler {
    //TODO: adds message to buffer so that they arrive in the same order. implemented as queue.
    private LinkedList<Message> messageQueue;

    public MessageHandler() {
        this.messageQueue = new LinkedList<>();
    }

    /**
     * add message to queue
     * @param message instance of message
     */
    public synchronized void enqueue(Message message) {
        messageQueue.addLast(message);
    }

    /**
     * @return first message in queue or null if queue empty
     */
    public synchronized Message dequeue() {
        if (!messageQueue.isEmpty()) {
            return messageQueue.removeFirst();
        }
        return null;
    }

    /**
     * @return if queue is empty
     */
    public synchronized boolean hasMessages() {
        return !messageQueue.isEmpty();
    }

}
