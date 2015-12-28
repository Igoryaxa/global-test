package com.global.test.dao;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;

import javax.annotation.PreDestroy;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.global.test.exception.ApplicationException;
import com.global.test.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@Component
@Profile("dev")
public class FileUserDao extends AbstractInMemoryUserDao {
	@Value("${db.file.path}")
	private String pathToFile;
	
    private Gson gson = new GsonBuilder().create();

	@Override
	protected Collection<User> loadUsers() {
		Collection<User> users = new LinkedList<>();

		File file = checkAndGetFile();
		
		String fileContent;
		try {
			fileContent = FileUtils.readFileToString(file);
		} catch (IOException e) {
			throw new ApplicationException(e);
		}
		
        Type collectionType = new TypeToken<Collection<User>>(){}.getType();
        Collection<User> fileUsers = gson.fromJson(fileContent, collectionType);
        if (fileUsers != null) {
    		users.addAll(fileUsers);
		}
        
        return users;
	}
	
	@PreDestroy
	private void storeUsers() {
		String json = gson.toJson(getUsers());
		File file = checkAndGetFile();
		try {
			FileUtils.writeStringToFile(file, json);
		} catch (IOException e) {
			throw new ApplicationException(e);
		}
	}	

	private File checkAndGetFile() throws ApplicationException {
		File file = new File(pathToFile);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new ApplicationException(e);
			}
		}
		return file;
	}

}
