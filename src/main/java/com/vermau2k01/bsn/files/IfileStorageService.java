package com.vermau2k01.bsn.files;

import com.vermau2k01.bsn.books.Books;
import org.springframework.web.multipart.MultipartFile;

public interface IfileStorageService {
    String saveFile(MultipartFile file, Books book, Integer id);
}
