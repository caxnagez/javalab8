package javacax;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

public class Main {
    public static void main(String[] args) {

        //1
        PPPMethods A = new PPPMethods();

        for (var method : PPPMethods.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Repeat.class)) {
                Repeat repeat = method.getAnnotation(Repeat.class);
                method.setAccessible(true); // for private
                for (int i = 0; i < repeat.count(); i++) {
                    try {
                        method.invoke(A);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        //2
        String name = "Ilya"; 
        String surname = "malash"; 

        //Создание dir Malash
        Path dir = Paths.get(surname);
        try {
            Files.createDirectory(dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // Создание файла Ilia
        Path file = dir.resolve(name);
        try {
            Files.createFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Создание dir1-3 и копирование Ilya
        try {
            Files.createDirectories(dir.resolve("dir1"));
            Files.createDirectories(dir.resolve("dir2"));
            Files.createDirectories(dir.resolve("dir3"));

            Files.copy(file, dir.resolve("dir1").resolve(name), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(file, dir.resolve("dir2").resolve(name), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(file, dir.resolve("dir3").resolve(name), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Создание dir 1 dir 2
        try {
            Files.createFile(dir.resolve("dir1").resolve("file1"));
            Files.createFile(dir.resolve("dir2").resolve("file2"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //рекурсия и печать
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    System.out.println("F " + file.getFileName());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    System.out.println("D " + dir.getFileName());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Удаление dir 1
        try {
            Files.walkFileTree(dir.resolve("dir1"), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}



@Retention(RetentionPolicy.RUNTIME)
@interface Repeat {
    int count() default 2;
}

class PPPMethods {

    public void pub() {
        System.out.println("public 1");
    }

    public void pub2() {
        System.out.println("public 2");
    }

    @Repeat(count = 3)
    protected void protect() {
        System.out.println("protected 1");
    }

    @Repeat()
    protected void protect2() {
        System.out.println("protected 2");
    }

    @Repeat(count = 3)
    private void priv() {
        System.out.println("private 1");
    }

    private void priv2() {
        System.out.println("private 2");
    }
}