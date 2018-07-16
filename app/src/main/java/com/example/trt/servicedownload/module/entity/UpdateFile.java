package com.example.trt.servicedownload.module.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Trt on 2018/6/24.
 */
@Entity(nameInDb = "update_file",indexes = {
        @Index(value = "filename DESC", unique = true)
})
public class UpdateFile {
    @Id private Long id;
    private String filename;
@Generated(hash = 1650125454)
public UpdateFile() {
}
@Generated(hash = 701890043)
public UpdateFile(Long id, String filename) {
    this.id = id;
    this.filename = filename;
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}
public String getFilename() {
    return this.filename;
}
public void setFilename(String filename) {
    this.filename = filename;
}
}
