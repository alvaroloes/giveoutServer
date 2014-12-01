/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.capstone.giveout.controllers;

import com.capstone.giveout.Constants;
import com.capstone.giveout.Routes;
import com.capstone.giveout.models.User;
import com.capstone.giveout.models.UserRepository;
import com.capstone.giveout.util.ImageFileManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.util.UriEncoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.util.Collection;

@Controller
public class UsersController {
    private static final String USERS_ROOT_PATH = StringUtils.stripStart(Routes.USERS_PATH, "/");

	@Autowired
	private UserRepository users;
    @Autowired
    private UserDetailsManager userDetailsManager;

    @PreAuthorize("hasRole(mobile)")
	@RequestMapping(value = Routes.CURRENT_USER_PATH, method=RequestMethod.GET)
    public @ResponseBody User getCurrent(Principal p) {
        return users.findByUsername(p.getName());
    }

	@RequestMapping(value = Routes.USERS_PATH, method = RequestMethod.POST)
	public @ResponseBody User create(
            @RequestParam("user") String userString,
            @RequestParam MultipartFile image) throws IOException {
        // In order to send in one request the user data and the image, the user data must be
        // sent as a json string encoded along with the image.
        // Here we parse it into a User object.
        User user = new ObjectMapper().readValue(UriEncoder.decode(userString), User.class);

        UserDetails u = com.capstone.giveout.auth.User.create(user.getUsername(), user.getPassword(), "USER");
        userDetailsManager.createUser(u);

		user = users.findByUsername(user.getUsername());
        saveImages(user, image);

        return user;
	}

    @RequestMapping(value = Routes.TOP_GIVERS_PATH, method=RequestMethod.GET)
    public @ResponseBody Collection<User> getTop(
           @RequestParam(value = Routes.TOP_KIND_PARAMETER, required = false, defaultValue = Constants.DEFAULT_TOP_KIND) String kind,
           @RequestParam(value = Routes.PAGE_PARAMETER, required = false, defaultValue = "0") int page,
           @RequestParam(value = Routes.LIMIT_PARAMETER, required = false, defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit) {
        PageRequest pageRequest = new PageRequest(page, limit);

        if (Constants.TOP_KIND_GIFT_COUNT.equals(kind)) {
            return Lists.newArrayList(users.getUsersOrderedByNumberOfGiftsDesc(pageRequest));
        } else {
            return Lists.newArrayList(users.getUsersOrderedByNumberOfTouchesDesc(pageRequest));
        }
    }

    @RequestMapping(value = Routes.USERS_IMAGE_PATH, method = RequestMethod.GET)
    public @ResponseBody void getImage(
            @PathVariable("id") long id,
            @PathVariable("size") String size,
            HttpServletResponse response) throws IOException {
        User user = users.findOne(id);
        if( user == null) {
            response.sendError(HttpStatus.NOT_FOUND.value());
            return;
        }

        try {
            ImageFileManager.get(USERS_ROOT_PATH).copyImage(user.getId(), size, response.getOutputStream());
        } catch (FileNotFoundException e) {
            response.sendError(HttpStatus.NOT_FOUND.value());
        }
    }

    private void saveImages(User user, MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) return;

        BufferedImage img = ImageIO.read(image.getInputStream());
        ImageFileManager imgMan = ImageFileManager.get(USERS_ROOT_PATH);

        imgMan.saveImage(user.getId(), User.SIZE_FULL, img);
        user.setImageUrlFull(Routes.USERS_IMAGE_PATH.replace("{id}", String.valueOf(user.getId())).replace("{size}", User.SIZE_FULL));

        BufferedImage scaledImg = Scalr.resize(img, 640);
        imgMan.saveImage(user.getId(), User.SIZE_MEDIUM, scaledImg);
        user.setImageUrlMedium(Routes.USERS_IMAGE_PATH.replace("{id}", String.valueOf(user.getId())).replace("{size}", User.SIZE_MEDIUM));

        users.save(user);
    }
}
