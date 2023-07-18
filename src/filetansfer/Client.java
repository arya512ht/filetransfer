
package filetansfer;

/**
 *
 * @author Arya
 */
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    private static final String SERVER_IP = "localhost";
    private static final int PORT = 4000;
    private static final String FILE_PATH = "D:\\";

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter file name to transfer: ");
            String fileName = scanner.nextLine();

            File file = new File(FILE_PATH + fileName);

            Socket socket = new Socket(SERVER_IP, PORT);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            outputStream.writeUTF(fileName);

            boolean resume = false;
            if (inputStream.readBoolean() == true) 
            {

                System.out.println("Do you want to resume the download? (Y/N)");
                String resumeChoice = scanner.nextLine().toUpperCase();
                if (resumeChoice.equals("Y")) 
                {
                    resume = true;
                    outputStream.writeBoolean(true);
                    long currentByte = file.length();
                    outputStream.writeLong(currentByte);
                }
                if (resumeChoice.equals("N")) 
                    continue;
            } 
            else 
            {
                System.out.println("File Not Found !!");
                continue;
            }
            long fileSize = inputStream.readLong();
            FileOutputStream fileOutputStream;

            if (resume) 
            {
                fileOutputStream = new FileOutputStream(file, true);
            } else {
                fileOutputStream = new FileOutputStream(file);
            }

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            fileOutputStream.close();
            inputStream.close();
            outputStream.close();
            socket.close();

            System.out.println("File transfer complete: " + fileName);

            System.out.println("Do you want to transfer another file? (Y/N)");
            String choice = scanner.nextLine().toUpperCase();
            if (choice.equals("N")) {
                break;
            }
        }
        scanner.close();
    }
}