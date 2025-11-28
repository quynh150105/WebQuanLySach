package com.thunga.web.seed;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thunga.web.entity.Author;
import com.thunga.web.entity.Book;
import com.thunga.web.entity.Category;
import com.thunga.web.repository.AuthorRepository;
import com.thunga.web.repository.BookRepository;
import com.thunga.web.repository.CategoryRepository;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public void seed() throws Exception {
        if (bookRepository.count() == 0) {
            seedBooksFromImages();
        }
    }

    private void seedBooksFromImages() throws IOException {

        // Load tất cả file ảnh trong resources/static/images/books/*
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:/static/images/books/*");

        if (resources.length == 0) {
            System.out.println("⚠ Không tìm thấy ảnh trong /static/images/books/");
            return;
        }

        // Tạo Author mặc định
        Author defaultAuthor = authorRepository.findByName("Chưa rõ");
        if (defaultAuthor == null) {
            defaultAuthor = authorRepository.save(new Author(null, "Chưa rõ"));
        }

        // Tạo Category mặc định
        Category defaultCategory = categoryRepository.findByName("Khác");
        if (defaultCategory == null) {
            defaultCategory = categoryRepository.save(new Category(null, "Khác"));
        }

        for (Resource res : resources) {

            String fileName = res.getFilename();
            if (fileName == null) continue;

            // Tách title từ tên file
            String title = fileName.substring(0, fileName.lastIndexOf("."))
                    .replace("-", " ")
                    .replace("_", " ");

            // Nếu đã có rồi thì bỏ qua
            if (!bookRepository.findByTitle(title).isEmpty()) {
                continue;
            }

            byte[] imageBytes = res.getInputStream().readAllBytes();

            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(defaultAuthor);
            book.setCategory(defaultCategory);
            book.setImage(imageBytes);

            // --- Giá trị mặc định để tránh lỗi UI ---
            book.setPrice(99000);
            book.setNumber_in_stock(10);
            book.setNumber_sold(0);
            book.setNumber_page(120);
            book.setDate_publication("2024");
            book.setDescription("Sách chưa có mô tả chi tiết.");

            bookRepository.save(book);

            System.out.println("✔ Seeded book: " + title);
        }
    }
}
