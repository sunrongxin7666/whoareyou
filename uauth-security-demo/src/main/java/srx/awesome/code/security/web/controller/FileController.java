package srx.awesome.code.security.web.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import srx.awesome.code.security.dto.FileInfo;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import static org.apache.commons.io.IOUtils.*;

@RestController
@RequestMapping("/file")
public class FileController {
    String path = "/Users/sunrongxin/IdeaProjects/wohareyou/uauth-security-demo/src/main/upload";

    @PostMapping
    public FileInfo upload(MultipartFile file) throws IOException {
        System.out.println(file.getName());
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getSize());

        File localFile = new File(path, new Date().getTime() + ".txt");

        //MultipartFile类的特性
        file.transferTo(localFile);

        return  new FileInfo(localFile.getAbsolutePath());
    }

    @GetMapping("/{id}")
    public void Download(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        File localFile = new File(path, id+ ".txt");
        //java 7的特性
        try (FileInputStream inputStream = new FileInputStream(localFile);
             ServletOutputStream outputStream = response.getOutputStream()){
            response.setContentType("application/x-download");
            response.addHeader("Content-Dispostion","attachment;filename-test.txt");

            //common-io IOUtils
            copy(inputStream,outputStream);
            outputStream.flush();
        } catch (Exception e){

        }



    }
}
