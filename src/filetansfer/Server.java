
package filetansfer;

/**
 *
 * @author Arya
 */
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int PORT = 4000;
    private static final String FILE_PATH = "C:\\Users\\Arya\\Downloads\\";

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        while (true) {
            Socket socket = serverSocket.accept();
            executorService.execute(new FileTransferHandler(socket));
        }
    }

    private static class FileTransferHandler implements Runnable {

        private Socket socket;
        private DataInputStream inputStream;
        private DataOutputStream outputStream;
        private FileInputStream fileInputStream;
        private BufferedInputStream bufferedInputStream;
        private byte[] buffer;

        public FileTransferHandler(Socket socket) {
            this.socket = socket;
            this.buffer = new byte[4096];
        }

        @Override
        public void run() {
            try {
                this.inputStream = new DataInputStream(socket.getInputStream());
                this.outputStream = new DataOutputStream(socket.getOutputStream());

                String fileName = inputStream.readUTF();
                File file = new File(FILE_PATH + fileName);

                if (!file.exists()) {
                    System.out.println("File does not exist!");

                    outputStream.writeBoolean(false);
                }

                long fileSize = file.length();
                
                outputStream.writeBoolean(true);

                long currentByte = 0;
                boolean resume = false;

                if (inputStream.readBoolean()) {
                    currentByte = inputStream.readLong();

                    resume = true;
                }

                outputStream.writeLong(fileSize);
                outputStream.flush();

                if (resume) {
                    fileInputStream = new FileInputStream(file);
                    bufferedInputStream = new BufferedInputStream(fileInputStream);
                    bufferedInputStream.skip(currentByte);
                } else {
                    fileInputStream = new FileInputStream(file);
                    bufferedInputStream = new BufferedInputStream(fileInputStream);
                }

                int bytesRead;
                while ((bytesRead = bufferedInputStream.read(buffer, 0, buffer.length)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    currentByte += bytesRead;
                }

                bufferedInputStream.close();
                fileInputStream.close();
                outputStream.close();
                inputStream.close();
                socket.close();

                System.out.println("File transfer complete: " + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
