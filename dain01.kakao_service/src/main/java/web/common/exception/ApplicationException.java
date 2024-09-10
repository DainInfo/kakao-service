package web.common.exception;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import web.AppConstants;
import web.common.util.NullUtil;
import lombok.Getter;

/**
 * @Project : SCUP
 * @Class : ApplicationException
 * @Description : Server Error(500)
 * @Author : im7015
 * @Since : 2019-05-10
 */
@Getter
public class ApplicationException extends RuntimeException {

    private final String code;

    private final String message;

    private final String detailMessage;

    private final Exception exception;

    public ApplicationException(String message) {
        this(null, AppConstants.FAIL, message, null);
    }

    public ApplicationException(String code, String message) {
        this(null, code, message, null);
    }

    public ApplicationException(Exception e) {
        this(e, null, null, null);
    }

    public ApplicationException(Exception e, String message) {
        this(e, AppConstants.FAIL, message, null);
    }

    public ApplicationException(Exception e, String code, String message) {
        this(e, code, message, null);
    }

    public ApplicationException(Exception e, String code, String message, String detailMessage) {
        super();
        this.exception = e;
        this.code = code;

        if (StringUtils.isEmpty(message) && e != null) {
            this.message = e.getMessage();
        } else {
            this.message = message;
        }

        if (StringUtils.isEmpty(detailMessage) && e != null) {
            this.detailMessage = e.getMessage();
        } else {
            this.detailMessage = detailMessage;
        }
    }

    public final Exception getException() {
        return exception;
    }

    public final String getCode() {
        return code;
    }

    @Override
    public final String getMessage() {
        if (NullUtil.isNotNull(this.exception)) {
            return message + "(ORIGINAL EXCEPTION : " + ExceptionUtils.getMessage(this.exception) + ")";
        }
        return message;
    }

    public final String getDetailMessage() {
        return detailMessage;
    }
}
