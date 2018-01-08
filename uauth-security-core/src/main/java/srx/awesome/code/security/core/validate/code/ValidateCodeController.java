package srx.awesome.code.security.core.validate.code;

import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

@RestController
public class ValidateCodeController {

    public static final String SESSION_KEY_IMAGE_CODE = "SESSION_KEY_IMAGE_CODE";
    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    @GetMapping("/code/image")
    public void createdCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //根据随机数生成图片；
        ImageCode imageCode = createImageCode(request);
        //将随机数保存在Session中；
        sessionStrategy.setAttribute(new ServletWebRequest(request),SESSION_KEY_IMAGE_CODE,imageCode);
        //将生成的图片写到API的响应中
        ImageIO.write(imageCode.getImage(),"JPEG",response.getOutputStream());
    }

    /**
     * 生成图片验证码
     * @param request
     * @return
     */
    private ImageCode createImageCode(HttpServletRequest request) {
        int width =67;
        int height = 23;
        BufferedImage image =new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);

        Graphics g = image.getGraphics();

        Random random = new Random();

        g.setColor(getRandColor(200,250));
        g.fillRect(0,0,width,height);
        g.setFont(new Font("Time NEw Roman", Font.ITALIC,20));
        g.setColor(getRandColor(200,250));


        //生成干扰的条文
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);

            int xl = random.nextInt(12);
            int yl = random.nextInt(12);

            g.drawLine(x,y,x+xl,y+yl);
        }


        //生成随机码数字
        String sRand ="";
        for (int i = 0; i < 4; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand += rand;
            g.setColor(new Color(20+ random.nextInt(110),
                    20+ random.nextInt(110),
                    20+ random.nextInt(110)));

            g.drawString(rand,13*i+6, 16);
        }

        g.dispose();

        return new ImageCode(image,sRand,60);
    }


    /**
     * 为干扰条纹随机产生颜色
     * @param fc
     * @param bc
     * @return
     */
    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if(fc > 255){
            fc = 255;
        }

        if(bc > 255){
            bc = 255;
        }

        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);

        return new Color(r,g,b);
    }
}
