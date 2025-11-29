package ru.yandex.practicum.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
//    Throwable throwable; //1
//    StackTraceElement[] stackTrace; //2
//    HttpStatus httpStatus = HttpStatus.NOT_FOUND; //3
//    String userMessage; //4
//    String message; //5
//    Throwable[] suppressed; //6
//    String localizedMessage; //7
//
//    public ProductNotFoundException(String message, Throwable cause) {
//        super(message, cause);
//    }
}


//@Getter
//@Setter
//@JsonInclude(JsonInclude.Include.NON_NULL)
//public class ProductNotFoundException extends RuntimeException {
//
//    // Изменяем названия полей, чтобы не конфликтовать с методами Throwable
//    @JsonProperty("cause")
//    private ErrorDetail causeDetail;
//
//    @JsonProperty("stackTrace")
//    private List<StackTraceElementDetail> stackTraceDetails;
//
//    private String httpStatus;
//    private String userMessage;
//
//    // Переименовываем message, чтобы не конфликтовать с getMessage()
//    @JsonProperty("message")
//    private String errorMessage;
//
//    @JsonProperty("suppressed")
//    private List<ErrorDetail> suppressedDetails;
//
//    private String localizedMessage;
//    private LocalDateTime timestamp;
//
//    public ProductNotFoundException(String message) {
//        super(message);
//        initializeResponseFields("Продукт не найден", HttpStatus.NOT_FOUND);
//    }
//
//    public ProductNotFoundException(String message, String userMessage) {
//        super(message);
//        initializeResponseFields(userMessage, HttpStatus.NOT_FOUND);
//    }
//
//    public ProductNotFoundException(String message, Throwable cause) {
//        super(message, cause);
//        initializeResponseFields("Продукт не найден", HttpStatus.NOT_FOUND);
//    }
//
//    public ProductNotFoundException(Long productId) {
//        super(String.format("Продукт с ID %d не найден", productId));
//        initializeResponseFields("Запрашиваемый продукт не найден", HttpStatus.NOT_FOUND);
//    }
//
//    private void initializeResponseFields(String userMessage, HttpStatus httpStatus) {
//        this.timestamp = LocalDateTime.now();
//        this.httpStatus = httpStatus.toString();
//        this.userMessage = userMessage;
//        this.localizedMessage = super.getLocalizedMessage();
//        this.errorMessage = super.getMessage();
//
//        // Заполняем causeDetail (не cause!)
//        if (super.getCause() != null) {
//            this.causeDetail = new ErrorDetail(super.getCause());
//        }
//
//        // Заполняем stackTraceDetails (не stackTrace!)
//        if (super.getStackTrace() != null) {
//            this.stackTraceDetails = Arrays.stream(super.getStackTrace())
//                    .map(StackTraceElementDetail::new)
//                    .collect(Collectors.toList());
//        }
//
//        // Заполняем suppressedDetails (не suppressed!)
//        if (super.getSuppressed() != null && super.getSuppressed().length > 0) {
//            this.suppressedDetails = Arrays.stream(super.getSuppressed())
//                    .map(ErrorDetail::new)
//                    .collect(Collectors.toList());
//        }
//    }
//
//    // Внутренние классы для структуры JSON
//    @Getter
//    @Setter
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    public static class ErrorDetail {
//        @JsonProperty("stackTrace")
//        private List<StackTraceElementDetail> stackTraceDetails;
//        private String message;
//        private String localizedMessage;
//
//        public ErrorDetail(Throwable throwable) {
//            this.message = throwable.getMessage();
//            this.localizedMessage = throwable.getLocalizedMessage();
//
//            if (throwable.getStackTrace() != null) {
//                this.stackTraceDetails = Arrays.stream(throwable.getStackTrace())
//                        .limit(10)
//                        .map(StackTraceElementDetail::new)
//                        .collect(Collectors.toList());
//            }
//        }
//    }
//
//    @Getter
//    @Setter
//    public static class StackTraceElementDetail {
//        private String classLoaderName;
//        private String moduleName;
//        private String moduleVersion;
//        private String methodName;
//        private String fileName;
//        private int lineNumber;
//        private String className;
//        private boolean nativeMethod;
//
//        public StackTraceElementDetail(StackTraceElement element) {
//            this.classLoaderName = "app";
//            this.moduleName = element.getModuleName();
//            this.moduleVersion = element.getModuleVersion();
//            this.methodName = element.getMethodName();
//            this.fileName = element.getFileName();
//            this.lineNumber = element.getLineNumber();
//            this.className = element.getClassName();
//            this.nativeMethod = element.isNativeMethod();
//        }
//    }
//}
