//
//  MUConnection.java
//  JavaTelnet
//
//  Created by asp on Fri Nov 16 2001.
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine;

import net.sourceforge.btthud.ui.Thud;
import net.sourceforge.btthud.util.LineHolder;

import java.net.*;
import java.io.*;


/**
 * This class is for handling the basic connection between the HUD and the MUX. 
 *
 * @author Anthony Parker
 * @version 1.0, 11.16.01
 */

public class MUConnection extends Thread {

    
    /*
     * Holds the connection.
     */
    Socket			conn = null;
    String			host = null;
    //MUParse			handler = null;
    Thud			errorHandler = null;
    int				port;
    BufferedReader	rd;
    InputStream		is;
    BufferedWriter	wr;
    public boolean	connected = false;
    boolean			go = true;
    
    private Thread	connThread = null;

    LineHolder		lh = null;

    /**
     * Creates a new MUConnection object, connects to the host, and starts recieving data and storing it.
     * @param host The IP address or name of the host we're connecting to.
     * @param port The port of the host we're connecting to.
     * @see check
     * @see endConnection
     */
    public MUConnection(LineHolder lh, String host, int port, Thud errorHandler) throws java.net.UnknownHostException, java.io.IOException
    {
        this.lh = lh;
        this.host = host;
        this.port = port;
        //this.handler = handler;
        this.errorHandler = errorHandler;
        
        try
        {
            conn = new Socket(host, port);

            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            wr = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            this.connected = true;
            start();
        }
        catch (Exception e)
        {
            // throw e;
        }
    }
    
    /**
     * Start the MUConnection thread going...
     */
    public void start()
    {
        if (connThread == null)
        {
            connThread = new Thread(this, "MUConnection");
            connThread.start();
        }
    }
    
    /**
     * Send a string to the MUX, usually to execute a command.
     * @param command The string which holds the command we want to run.
     */    
    public void sendCommand(String command) throws java.io.IOException
    {
        try
        {
            wr.write(command);
            wr.newLine();
            wr.flush();
        }
        catch (SocketException e) {
        	// If we get a socket exception - disconnect.
        	// This is kinda hacked up - ideally the thread communication would work a little better.
        	// But, it prevents a bug where if the remote connection is closed, THUD goes to 100% cpu and throws
        	// SocketExceptions all the livelong day :)
        	System.out.println("Lost connection - aborting");
        	errorHandler.doStop();
        	errorHandler.stopConnection();        	
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e);
        }
    }
    
    /**
     * Checks to see if there is new input that we should store here. If so, it puts it in the StringBuffer.
     */
    public void run()
    {        
        while (go)
        {
            try
            {
                //handler.queueLine(rd.readLine());
                lh.put(rd.readLine());
            }
            catch (IOException ioe)
            {
                errorHandler.stopConnection();
            }
            catch (Exception e)
            {
                System.out.println("Error: connection: " + e);
            }
        }
    }
    
    public void pleaseStop()
    {
        try
        {
            conn.close();			// close the socket
            this.connected = false;
        }
        catch (Exception e)
        {
            System.out.println("Error: closeSocket: " + e);
        }
        
        go = false;
    }
}