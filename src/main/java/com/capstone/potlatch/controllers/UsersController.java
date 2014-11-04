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

package com.capstone.potlatch.controllers;

import com.capstone.potlatch.Routes;
import com.capstone.potlatch.models.User;
import com.capstone.potlatch.models.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
public class UsersController {

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
	public @ResponseBody User create(@RequestBody User user)
    {
//        ShaPasswordEncoder passwordEncoder = new ShaPasswordEncoder();
//        String encodedPassword = passwordEncoder.encodePassword(user.getPassword(), null);
        UserDetails u = com.capstone.potlatch.auth.User.create(user.getUsername(), user.getPassword(), "USER");
        userDetailsManager.createUser(u);
		return users.findByUsername(user.getUsername());
	}
}