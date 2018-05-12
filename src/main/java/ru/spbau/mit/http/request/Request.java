package ru.spbau.mit.http.request;

import java.util.List;

public final class Request {

    public RequestLine getRequestLine() {
        return requestLine;
    }

    private RequestLine requestLine;
    private List<Headers> headersList;

    public Request(RequestLine requestLine) {
        this.requestLine = requestLine;
    }

    public static Request parse(String stringRequest) {
        int begin = 0;
        int end = 0;
        while (end < stringRequest.length() &&
                Character.compare(stringRequest.charAt(end), (char) Character.LINE_SEPARATOR) != 0) {
            ++end;
        }
//        ++end;
        return new Request(RequestLine.parse(stringRequest.substring(begin, end)));
    }

    public static final class Method {
        public static final Method GET = new Method("GET");
        // TODO addPeer more methods if needed
        private String method;

        public Method(String method) {
            this.method = method;
        }

        public String getString() {
            return method;
        }

//        @Override
//        public String toString() {
//            return method;
//        }

        public static Method parse(String methodString) {
            switch (methodString.trim()) {
                case "GET":
                    return GET;
                default:
                    throw new IllegalStateException("undefined method");
            }
        }
    }

    public static final class RequestURI {
        public enum TYPE {
            ABSOLUTE_URI,
            ABS_PATH,
            AUTHORITY,
            ASTERISK
        }

        private TYPE type;

        public String getUri() {
            return uri;
        }

        private String uri;

        public RequestURI(TYPE type, String uri) {
            this.type = type;
            this.uri = uri;
        }

        public String getString() {
            return uri;
        }

//        @Override
//        public String toString() {
//            return uri;
//        }

        public static RequestURI parse(String stringRequestURI) {
            return new RequestURI(TYPE.ABSOLUTE_URI, stringRequestURI.trim());
        }
    }

    public static final class RequestLine {
        private Method method;
        private RequestURI requestURI;
        private String httpVersion;

        public RequestLine(Method method, RequestURI requestURI, String httpVersion) {
            this.method = method;
            this.requestURI = requestURI;
            this.httpVersion = httpVersion;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
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

        public static RequestLine parse(String stringRequestLine) {

            int begin = 0;
            int end = 0;
            while (end < stringRequestLine.length() &&
                    !Character.isWhitespace(stringRequestLine.charAt(end))) {
                ++end;
            }
            ++end;
            Method method = Method.parse(stringRequestLine.substring(begin, end));

            begin = end;
            while (end < stringRequestLine.length() &&
                    !Character.isWhitespace(stringRequestLine.charAt(end))) {
                ++end;
            }
            ++end;
            RequestURI requestURI = RequestURI.parse(stringRequestLine.substring(begin, end));

            begin = end;
            while (end < stringRequestLine.length() &&
                    !Character.isSpaceChar(stringRequestLine.charAt(end))) {
                ++end;
            }
//            ++end;

            String httpVersion = stringRequestLine.substring(begin, end);
            return new RequestLine(method, requestURI, httpVersion);
        }

        public String getString() {
            return method.getString() + " " + requestURI.getString() + " "
                    + httpVersion + System.lineSeparator();
        }

//        @Override
//        public String toString() {
//            return method.toString() + " " + requestURI.toString() + " "
//                    + httpVersion + System.lineSeparator();
//        }

    }

    public static final class RequestHeader {
        //TODO addPeer fields(if needed!) from https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html#sec5
    }

    public static final class GeneralHeader {
        //TODO addPeer fields (if needed!) from https://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.5
    }

    public static final class EntityHeader {
        //TODO addPeer fields (if needed!) from https://www.w3.org/Protocols/rfc2616/rfc2616-sec7.html#sec7.1
    }

    public static final class Headers {
        private RequestHeader requestHeader;
        private GeneralHeader generalHeader;
        private EntityHeader entityHeader;
    }

    public static final class MessageBody {
        //TODO addPeer fields (if needed!) from https://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.3
    }

    public String getString() {
        return requestLine.getString();
    }

//    @Override
//    public String toString() {
//        return requestLine.toString();
//    }
}
