package exception;

public class NoDataAvailableException extends Exception {
    public String message;
    public NoDataAvailableException(String message){
        this.message=message;
    }
}
