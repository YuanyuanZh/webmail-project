package cs601.webmail.pojo;

/**
 * Created by yuanyuan on 10/24/14.
 */

import java.io.Serializable;



public class Mail implements Serializable {

    private String accountId;

    private String subject;

    private String from;

    private String to;

    private String date;

    private String contentType;

    private String body;

    // flags
    private boolean unread = true;

    private boolean removed = false;



}

