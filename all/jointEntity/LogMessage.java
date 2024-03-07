package all.jointEntity;

public class LogMessage {
    private User user;
    private Message message;
    private ContactsMessage contactsMessage;

  public LogMessage(User user){
    this.user = user;
  }

  public LogMessage(Message message){
    this.message = message;
  }

  public LogMessage(ContactsMessage contactsMessage){
    this.contactsMessage = contactsMessage;
  }

    public User getUser(){
        return user;
    }

    public Message getMessage(){
        return message;
    }

    public ContactsMessage getContactsMessage(){
        return contactsMessage;
    }



}
