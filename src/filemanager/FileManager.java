/*
File manager lab
@author: Huy Nguyen
Date 20/8/2021
Version 1.0
 */
package filemanager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class FileManager {
    public static void main(String[] args){
        boolean output = true;
        int option;

        //test case trên macos
        //Path currentPath = Paths.get("Users/huy/Desktop/test");

        // Nhập path trên window
        System.out.println("Type the path you want to work with: ");
        Scanner s = new Scanner(System.in);
        Path currentPath = Paths.get(s.nextLine());


        String temp = "";
        do{
            printMenu(currentPath);
            Scanner sc = new Scanner(System.in);
            option = Integer.parseInt(sc.nextLine());
            switch(option){
                case 1:
                    currentPath = goUp(currentPath);
                    break;
                case 2:
                    currentPath = goDown(currentPath);
                    break;
                case 3:
                    currentPath = currentPath.getRoot();
                    break;
                case 4:
                    currentPath = searchFile(currentPath.getRoot(), 10);  // chú ý permitted tùy theo máy! có thể thay currentPath = file root tùy theo permitted
                    // test case trên macos
                    //currentPath = searchFile(Paths.get("/Users/huy/Desktop"), 10);
                    break;
                case 5:
                    listFileWithoutChoice(currentPath);
                    break;
                case 6:
                    createFile(currentPath,getName());
                    break;
                case 7:
                    writeFile(currentPath);
                    break;
                case 8:
                    readFile(currentPath);
                    break;
                case 9:
                    moveFile(currentPath);
                    break;
                case 10:
                    temp = copyFile(currentPath);
                    break;
                case 11:
                    pasteFile(temp, currentPath);
                    temp = ""; // dán xong sẽ xóa
                    break;
                case 12:
                    deleteFile(currentPath);
                    break;
                case 13:
                    currentPath = searchFile(currentPath,10); // search from currentPath
                    break;
                case 14:
                    output = false;
                    break;
                default:
                    System.out.println("Invalid input!");
            }
            System.out.println("--------------------------------------------");
        }while(output);
    }

    // print menu
    public static void printMenu(Path currentPath){
        System.out.println();
        System.out.println();
        System.out.println("--------------------------------------------");
        System.out.println("Current Path is: " + currentPath);
        System.out.println("----------------File-Manager----------------");
        System.out.println("1.Go up                   2.Go down");
        System.out.println("3.Go to root              4.Go to new folder");
        System.out.println("5.List file and folder    6.Create file");
        System.out.println("7.Write file              8.Read file");
        System.out.println("9.Move file               10.Copy file");
        System.out.println("11.Paste file             12.Delete file");
        System.out.println("13.Search file            14.Exit");
        System.out.println("--------------------------------------------");
        System.out.println("Your choice: ");
    }

    // get file name
    public static String getName(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Type the file name you choose: ");
        return scan.nextLine();
    }

    // get index
    public static int getIndex(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Type the index of path: ");
        return Integer.parseInt(scan.nextLine());
    }

    // list file không kèm index, dùng trong trường hợp gõ tên hoặc path
    public static void listFileWithoutChoice(Path currentPath){
        try {
            Files.list(currentPath).filter(path -> !path.getFileName().endsWith(".DS_Store")).forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // list file đi kèm index để chọn , trả về list file trong folder hiện tại để thao tác sau khi chọn xong
    public static List<Path> listFileWithChoice(Path currentPath){
        try {
            List<Path> list = Files.list(currentPath).filter(path -> !path.getFileName().endsWith(".DS_Store")).toList();
            for(int i = 0; i < list.size(); i++){
                System.out.println(i + " " + list.get(i));
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Path goUp(Path currentPath){
        if(currentPath.getParent() == null){
            System.out.println("Can not move up");
            return currentPath;
        } else {
            return currentPath.getParent();
        }
    }

    public static Path goDown(Path currentPath){
        /*
        // lấy file theo tên, có thể gõ nhiều level 1 lúc cho nhanh
        listFileWithoutChoice(currentPath);
        Path choice = Paths.get(currentPath.toString(),getName());
        */

        // lấy file theo index xuống lần lượt từng folder
        List<Path> list = listFileWithChoice(currentPath);
        Path choice = list.get(getIndex());
        if(Files.exists(choice)){
            return choice;
        } else {
            System.out.println("File does not exits!");
            return currentPath;
        }
    }

    public static void createFile(Path currentPath, String newPath){
        try {
            if(Files.isDirectory(currentPath)){
                Path create = Paths.get(currentPath.toString(),newPath);

                if(Files.exists(create)) {
                    System.out.println("File already exist! Choose other name.");
                } else {
                    Files.createFile(create);
                    System.out.println("Create file success!");
                }

            } else {
                System.out.println("This is not a folder");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(Path currentPath){
        // tìm file muốn nhập
        //Path file = searchFile(currentPath,10);

        //nhập file ở folder hiện tại
        List<Path> list = listFileWithChoice(currentPath);
        Path file = list.get(getIndex());

        // check if directory không cho viết
        if(Files.isDirectory(file)){
            System.out.println("this is folder, you can not write anything!");
        }

        // không phải folder -> cho viết
        if(!Files.isDirectory(file)) {
            List<String> document = new ArrayList<String>();
            System.out.println("Please write your content below: (type exit then Enter to quit write file)");

            Scanner scan = new Scanner(System.in);
            boolean check;
            do {
                String text = scan.nextLine();
                if (text.equalsIgnoreCase("exit")) {
                    check = false;
                } else {
                    document.add(text);
                    check = true;
                }
            } while (check);

            // nhập document vào file
            try {
                Files.write(file, document, StandardCharsets.UTF_8);
                System.out.println("write file success!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void readFile(Path currentPath){
        List<Path> list = listFileWithChoice(currentPath);
        System.out.println("Choose file to read");
        Path fileToRead = list.get(getIndex());
        /*
        try {
            byte[] fileContents = Files.readAllBytes(fileToRead);
            for (byte b : fileContents) {
                System.out.print((char) b);
            }
        */
        if(Files.isDirectory(fileToRead)){
            System.out.println("this is a folder, please use go down to go deeper!");
        }
        if(!Files.isDirectory(fileToRead)) {
            try {
                List<String> fileContents = Files.readAllLines(fileToRead);
                for (String str : fileContents) {
                    System.out.print(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }

    // hàm tìm file , trả về path được chọn
    public static Path searchFile(Path searchPath, int deepLevel){
        try {
            // tìm file theo tên và deep level
            String fileName = getName();
            List<Path> list = Files.find(searchPath,deepLevel, (p,a) -> p.getFileName().toString().equals(fileName))
                    .map(p -> p.toAbsolutePath())
                    .toList();

            // liệt kê file tìm thấy
            for(int i = 0; i < list.size(); i++){
                System.out.println(i + " " + list.get(i));
            }

            // báo không tìm thấy file nếu sai tên
            if(list.size() == 0) {
                System.out.println("File not found!");
                return searchPath; // trả về path hiện tại nếu không tìm được file
            }
            return list.get(getIndex()); // tìm được -> trả về file tìm được

        } catch (IOException e) {
            e.printStackTrace();
            return searchPath; // trả về path hiện tại nếu có lỗi
        }
    }

    // xóa file trong path hiện tại
    public static void deleteFile(Path currentPath){
        // xóa file theo tên
        //listFileWithoutChoice(currentPath);
        //Path fileNeedToDelete = Paths.get(currentPath.toString(),getName());

        // xóa file theo index
        List<Path> list = listFileWithChoice(currentPath);
        Path fileNeedToDelete = list.get(getIndex());

        // check if file does not exist
        if(!Files.exists(fileNeedToDelete)){
            System.out.println("File does not exist!");
        } else { // file exist
            try {
                Files.delete(fileNeedToDelete);
                System.out.println(fileNeedToDelete.getFileName().toString() + " was deleted successfully!");
            } catch (DirectoryNotEmptyException e){
                System.out.println("File does not empty!"); // exception file không rỗng
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // move file function , di chuyển file được chọn ở folder hiện tại và di chuyển đến folder tùy thích
    public static void moveFile(Path currentPath){
        List<Path> list = listFileWithChoice(currentPath);
        System.out.println("File want to move");
        Path fileToMove = list.get(getIndex());
        System.out.println("Directory want to move");
        Path moveFolder = searchFile(currentPath.getRoot(),10);

        //test case trên macos
        //Path moveFolder = searchFile(Paths.get("/Users/huy/Desktop"),10);

        try {
            if(Files.isSameFile(moveFolder,currentPath)){
                System.out.println("Same directory, no need to move!");
            }
            if(!Files.isSameFile(moveFolder,currentPath)){
                Files.move(fileToMove,Paths.get(moveFolder.toString(),fileToMove.getFileName().toString()));
                System.out.println("Move success!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // copy function, trả lại path của file copy để dùng cho hàm paste
    public static String copyFile(Path currentPath){
        List<Path> list = listFileWithChoice(currentPath);
        System.out.println("Please choose file to copy");
        Path file = list.get(getIndex());
        String fileCopy = file.toString();
        String fileCopyName = fileCopy + "-save"; // tạo 1 file copy đuôi -save bên cạnh file gốc

        try {
            Files.copy(file, Paths.get(fileCopyName));
            System.out.println("Copy success!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileCopy;
    }

    // paste function, dùng path của file đã được copy dán vào folder hiện tại
    public static void pasteFile(String moveFile, Path currentPath){
        Path fileToMove = Paths.get(moveFile + "-save");
        String fileName = Paths.get(moveFile).getFileName().toString();

        if(Files.exists(fileToMove) && Files.exists(currentPath)){
            try {
                Files.move(fileToMove,Paths.get(currentPath.toString(),fileName));
                System.out.println("Paste success!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
