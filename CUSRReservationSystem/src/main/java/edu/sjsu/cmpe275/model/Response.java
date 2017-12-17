package edu.sjsu.cmpe275.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
* <p>This is the model class, to form the responses with specific codes and messages</p>
* <title>Group 6</title>
* @author  Suchishree Jena, Pranjali Shrivastava, Aashi Tiwari
* @version 1.0
* @since   2017-11-22 
*/
@XmlRootElement
public class Response {

    private int status;

    private String message;

    /**
     * Instantiates a new Response.
     */
    public Response(){}

    /**
     * Instantiates a new Response.
     *
     * @param status the status
     * @param message  the message
     */
    public Response(int status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setCode(int status) {
        this.status = status;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
