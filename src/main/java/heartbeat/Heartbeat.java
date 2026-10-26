package heartbeat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by mkhanwalkar on 12/21/15.
 */
public class Heartbeat {

    String targetHost ;
    int port ;
    int interval ;
    int numMisses;

    // A connected peer that sends nothing for this long is dropped.
    static final int READ_TIMEOUT_MS = 60000;
    // Upper bound on concurrently served peers, so connection floods can't exhaust threads.
    static final int MAX_CONNECTIONS = 64;
    private final java.util.concurrent.Semaphore connectionSlots =
            new java.util.concurrent.Semaphore(MAX_CONNECTIONS);



    public Heartbeat(int port, String targetHost, int interval , int numMisses)
    {
        this.targetHost = targetHost;
        this.port = port ;
        this.interval = interval ;
        this.numMisses = numMisses;
    }


    // try to connect to target host , if successful set up task to connect to host .
    // else set up server to listen to incoming connections .


    public void init()
    {
        Thread t = new Thread(()->{

            while (true)
            {
                for (int i=0;i<10;i++) {
                    if (connect())
                    {
                        i=0;  // resert the counter on every successful connection
                        sendHeartBeat();
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                server();
            }


        });

        t.start();




    }

    private void sendHeartBeat()
    {
        System.out.println("Connected as Client ");
       // Thread t = new Thread(()->{

            while(true)
            {
                out.println("Hello from client" + Thread.currentThread().getName());
                out.flush();
                try {
                    String s = input.readLine();
                    if (s==null)
                        break;
                    System.out.println(s);
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

     //   });

     //   t.start();


    }


    public void destroy()
    {

    }

    Socket s ;
    BufferedReader input ;
    PrintWriter out ;

    public boolean connect()
    {

        try {
            s = new Socket(targetHost, port);
            input =
                    new BufferedReader(new InputStreamReader(s.getInputStream()));

            out =
                    new PrintWriter(s.getOutputStream(), true);
            return true;

        } catch (IOException e) {
           // e.printStackTrace();
            return false ;
        }

    }

    public void server ()
    {

    //        Thread t = new Thread(()->{


                try {
                    ServerSocket listener = new ServerSocket(port);
                    try {
                        while(true) {
                             Socket socket = listener.accept();
                            if (!connectionSlots.tryAcquire()) {
                                socket.close();
                                continue;
                            }
                            System.out.println("Listening as server");
                           // Handler handler = (Handler) Class.forName(handlerType).newInstance();
                            //handler.setSocket(socket);
                         Thread t1 = new Thread(()->{

                             // One reader/writer per connection; stop (and free the thread) when the
                             // peer disconnects, errors or goes silent, instead of spinning forever.
                             try (Socket conn = socket;
                                  BufferedReader input =
                                          new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                  PrintWriter out =
                                          new PrintWriter(conn.getOutputStream(), true)) {
                                 conn.setSoTimeout(READ_TIMEOUT_MS);
                                 while (true) {
                                     String line = input.readLine();
                                     if (line == null)
                                         break;
                                     System.out.println(line);
                                     out.println("Hello from server" + Thread.currentThread().getName());
                                     out.flush();
                                 }
                             } catch (IOException e) {
                                 e.printStackTrace();
                             } finally {
                                 connectionSlots.release();
                             }


                           });
                            //Thread t1 = new Thread(handler);
                            t1.start();
                        }
                    }
                    finally {
                        listener.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }


          //  });

            //t.start();



        //}

    }

}
