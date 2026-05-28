package com.vanillacakes.cakes;

import com.vanillacakes.BadRequestException;
import com.vanillacakes.PagedResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public class CakeController extends HttpServlet {

    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final int DEFAULT_PAGE_SIZE = 9;
    private static final int MAX_PAGE_SIZE = 50;

    private final ObjectMapper mapper = new ObjectMapper();
    private final CakeRepository cakeRepository;

    public CakeController(CakeRepository cakeRepository) {
        this.cakeRepository = cakeRepository;
    }

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            handleFindCakes(req, resp);
            return;
        }

        handleFindCakeById(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CreateCakeRequest createCakeRequest =
                mapper.readValue(req.getInputStream(), CreateCakeRequest.class);

        Cake cake = new Cake(createCakeRequest.name(),
                createCakeRequest.description(),
                createCakeRequest.price(),
                createCakeRequest.active());
        cake = cakeRepository.save(cake);
        String cakeJson = mapper.writeValueAsString(cake);

        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write(cakeJson);
    }

    private void handleFindCakeById(HttpServletRequest req,
                                    HttpServletResponse resp)
            throws IOException {
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

        Cake cake = cakeRepository.findById(id);
        if (cake == null) {
            resp.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Cake not found"
            );
            return;
        }

        String cakeJson =
                mapper.writeValueAsString(cake);
        resp.setContentType("application/json");
        resp.getWriter().write(cakeJson);
    }

    private void handleFindCakes(HttpServletRequest req,
                                 HttpServletResponse resp)
            throws IOException {

        int pageNumber = DEFAULT_PAGE_NUMBER;
        int pageSize = DEFAULT_PAGE_SIZE;

        String pageNumberParam = req.getParameter("page");
        String pageSizeParam = req.getParameter("pageSize");
        try {
            if (pageNumberParam != null) {
                pageNumber = Integer.parseInt(pageNumberParam);
            }
            if (pageSizeParam != null) {
                pageSize = Integer.parseInt(pageSizeParam);
            }
            if (pageNumber <= 0 || pageSize <= 0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Pagination parameters must be positive");
                return;
            }
            if (pageSize > MAX_PAGE_SIZE) {
                pageSize = MAX_PAGE_SIZE;
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid pagination parameters");
            return;
        }

        PagedResult<Cake> cakes =
                cakeRepository.findCakes(pageNumber, pageSize);
        String cakesJson = mapper.writeValueAsString(cakes);

        resp.setContentType("application/json");
        resp.getWriter().write(cakesJson);
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
