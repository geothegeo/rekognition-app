package edu.gmu.cs321.rekognition;

import android.util.Base64;
import android.util.Base64OutputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class RekognitionClient {

    private URL rekogEndpoint;
    private HttpURLConnection rekogConnection;
    private final String urlString = "https://ctnxdvfrh1.execute-api.us-east-1.amazonaws.com/dev/analyzeImage";
    private static RekognitionClient instance = null;

    /**
     * Build a new RekognitionClient instance
     *
     * @throws MalformedURLException Can be thrown if the URL passed is malformed. It isn't though.
     * @throws IOException           Can be thrown if there's a connection issue. We can't handle it here, so let it propagate
     */
    private RekognitionClient() throws MalformedURLException, ProtocolException, IOException{
        rekogEndpoint = new URL(urlString);
        rekogConnection = (HttpURLConnection) rekogEndpoint.openConnection();
        rekogConnection.setRequestMethod("POST");
        rekogConnection.setRequestProperty("Content-Type", "application/json");
        rekogConnection.setDoOutput(true);
    }

    /**
     * Gets the sole instance of this object if it exists, or creates a new one
     *
     * @return A RekognitionClient instance
     * @throws IOException Passes this exception on from the constructor
     */
    public static RekognitionClient getInstance() throws IOException {
        if (instance != null) {
            return instance;
        } else {
            return instance = new RekognitionClient();
        }
    }

    /*    public static void main (String[] args) {

        try {

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            // Grab Image
            BufferedImage bImage = ImageIO.read(new File("hestia-cosplay.jpg"));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, "jpg", bos );
            byte [] data = bos.toByteArray();

            // Encode Image as Buffer Base64
            String encodedImg = Base64.getEncoder().encodeToString(data);

            // Attempt #1: JSON escape string
            // String input = "{\"qty\":100,\"name\":\"iPad 4\",\"img\":\"" + encodedImg + "\"}";

            // Attempt #2: JSON obj
            // JSONObject obj = new JSONObject();
            // obj.put("qty", new Integer(100));
            // obj.put("img", encodedImg);

            // Attempt #3
            String input = encodedImg;

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            System.out.println(HttpURLConnection.HTTP_CREATED);
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }*/

    /**
     * Writes Base64-encoded data to the underlying connection owned by this RekognitionClient instance
     * @param imageData The image data as a byte array
     */
    public void processImg(final byte[] imageData){

        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OutputStream rekogOut = rekogConnection.getOutputStream();

                    Base64OutputStream imageStream = new Base64OutputStream(rekogOut, Base64.DEFAULT);
                    /*String encoded = java.util.Base64.getEncoder().encodeToString(imageData);
                    System.out.println("ENCODED DATA: " + encoded);
                    rekogOut.write(encoded.getBytes());
                    rekogOut.close();*/
                    imageStream.write(imageData);
                    imageStream.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        myThread.start();
        try {
            myThread.join();
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves data from the Rekognition endpoint and returns it as a StringBuilder instance
     * @return A StringBuilder instance containing all the JSON data output from the Rekognition endpoint, can be empty
     */
    public StringBuilder retrieveData()
    {
        final StringBuilder rekogBuilder = new StringBuilder();
        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedReader rekogReader = new BufferedReader(new InputStreamReader(rekogConnection.getInputStream()));

                    String line = null;
                    while ((line = rekogReader.readLine()) != null) {
                        rekogBuilder.append(line);
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        });

        myThread.start();
        try{
            myThread.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return rekogBuilder;
    }

}