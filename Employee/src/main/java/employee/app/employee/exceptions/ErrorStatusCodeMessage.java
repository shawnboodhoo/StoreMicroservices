package store.app.store.exceptions;

import jdk.jfr.StackTrace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
@Getter
@Setter
@AllArgsConstructor
public class ErrorStatusCodeMessage {


        private int code;
        private String message;


    }