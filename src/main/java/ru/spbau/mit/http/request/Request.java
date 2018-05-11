package ru.spbau.mit.http.request;

import java.util.List;

public final class Request {

    private RequestLine requestLine;
    private List<Headers> headersList;

    public Request(RequestLine requestLine) {
        this.requestLine = requestLine;
    }

    public static final class Method {
        public static final String GET = "GET";
        // TODO add more methods if needed
    }

    public static final class RequestURI {
        public enum TYPE {
            ABSOLUTE_URI,
            ABS_PATH,
            AUTHORITY,
            ASTERISK
        }

        private TYPE type;
        private String uri;

        public RequestURI(TYPE type, String uri) {
            this.type = type;
            this.uri = uri;
        }

        @Override
        public String toString() {
            return uri;
        }
    }

    public static final class RequestLine {
        private String method;
        private RequestURI requestURI;
        private String httpVersion;

        public RequestLine(String method, RequestURI requestURI, String httpVersion) {
            this.method = method;
            this.requestURI = requestURI;
            this.httpVersion = httpVersion;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public RequestURI getRequestURI() {
            return requestURI;
        }

        public void setRequestURI(RequestURI requestURI) {
            this.requestURI = requestURI;
        }

        public String getHttpVersion() {
            return httpVersion;
        }

        public void setHttpVersion(String httpVersion) {
            this.httpVersion = httpVersion;
        }

    }

    public static final class RequestHeader {
        //TODO add fields(if needed!) from https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5
    }

    public static final class GeneralHeader {
        //TODO add fields (if needed!) from https://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.5
    }

    public static final class EntityHeader {
        //TODO add fields (if needed!) from https://www.w3.org/Protocols/rfc2616/rfc2616-sec7.html#sec7.1
    }

    public static final class Headers {
        private RequestHeader requestHeader;
        private GeneralHeader generalHeader;
        private EntityHeader entityHeader;
    }

    public static final class MessageBody {
        //TODO add fields (if needed!) from https://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.3
    }

    @Override
    public String toString() {
        return requestLine.method + " " + requestLine.requestURI + " "
                + requestLine.httpVersion + System.lineSeparator();
    }
}
