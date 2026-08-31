package com.vanillacakes.cakes;

import com.vanillacakes.BadRequestException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

public class CakeImageController extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();
    private final CakeImageService cakeImageService;

    public CakeImageController(CakeImageService cakeImageService) {
        this.cakeImageService = cakeImageService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id;
        try {
            id = extractId(req);

        } catch (BadRequestException e) {
            resp.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );
            return;
        }

        CakeImageContent cakeImageContent = cakeImageService.findByCakeId(id);
        if (cakeImageContent == null) {
            resp.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Cake image not found"
            );
            return;
        }

        resp.setContentType(cakeImageContent.mimeType());
        try (InputStream input = cakeImageContent.stream();
             OutputStream outputStream = resp.getOutputStream()) {
            input.transferTo(outputStream);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CreateCakeImageRequest createCakeImageRequest = mapper.readValue(req.getInputStream(),
                CreateCakeImageRequest.class);

        CakeImage cakeImage;
        try {
            cakeImage = new CakeImage(null,
                    createCakeImageRequest.cakeId(),
                    createCakeImageRequest.mimeType(),
                    Base64.getDecoder().decode(createCakeImageRequest.contentBase64()));
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Base64 content");
            return;
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_CREATED);
        mapper.writeValue(resp.getWriter(), new CreateCakeImageResponse(cakeImageService.save(cakeImage)));
    }

    private Long extractId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.length() <= 1) {
            throw new BadRequestException("Missing id in path");
        }

        try {
            String idStringWithoutSlash = pathInfo.substring(1);
            return Long.parseLong(idStringWithoutSlash);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid id in path");
        }
    }

}
