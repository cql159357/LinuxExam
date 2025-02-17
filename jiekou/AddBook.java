

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@WebServlet(urlPatterns = "/add")
public class AddBook extends HttpServlet {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://106.12.156.53/Linux_final?useUnicode=true"
            + "&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8";
    static final String USER = "root";
    static final String PASS = "Cql@123456";
    static final String SQL_BOOK_ADD = "INSERT INTO book( name , author ) VALUES (?,?)";
    private static Connection conn = null;

    public void init() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destory() {
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Book req = getRequestBody(request);
        getServletContext().log(req.toString());
        PrintWriter out = response.getWriter();

        out.println(addBook(req));
        out.flush();
        out.close();
    }

    private int addBook(Book req) {
        PreparedStatement stmt = null;
        int retcode = -1;
        try {
            stmt = conn.prepareStatement(SQL_BOOK_ADD);

            stmt.setString(1, req.name);
            stmt.setString(2, req.author);

            int row = stmt.executeUpdate();
            if (row > 0) {
                retcode = row;
            } else {
                retcode = 0;
            }
            stmt.close();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return retcode;
    }

    private Book getRequestBody(HttpServletRequest request) throws IOException {
        Book book = new Book();
        StringBuffer bodyj = new StringBuffer();
        String line = null;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            bodyj.append(line);
            Gson gson = new Gson();
            book = gson.fromJson(bodyj.toString(), new TypeToken<Book>() {
            }.getType());
        }
        return book;
    }


}

class Book {
    int id;
    String name;
    String author;

    public String toString() {
        return "BOOK:id=" + id + ",name=" + name + ",author=" + author + ".";

    }

    public void setId(int id) {

        this.id = id;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setAuthor(String author) {

        this.author = author;
    }

    public String getName() {
        return name;

    }

    public String getAuthor() {
        return author;
    }

    public Integer getId() {
        return id;

    }
}