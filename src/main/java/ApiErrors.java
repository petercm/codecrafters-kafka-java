public enum ApiErrors {
    UNSUPPORTED_VERSION((short) 35);

    private final short errorCode;

    ApiErrors(short i) {
        this.errorCode = i;
    }

    public short getErrorCode() {
        return errorCode;
    }
}
