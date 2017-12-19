package srx.awesome.code.security.exception;

public class UserNotExistException extends RuntimeException{
    public String id;
    public UserNotExistException(String id) {
        super("User:"+id+" not exist!");
        this.id = id;
    }
}
