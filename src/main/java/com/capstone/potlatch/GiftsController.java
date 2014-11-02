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

package com.capstone.potlatch;

import com.capstone.potlatch.models.User;
import com.google.common.collect.Lists;
import com.capstone.potlatch.models.Gift;
import com.capstone.potlatch.models.GiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;

@Controller
public class GiftsController {

	@Autowired
	private GiftRepository gifts;

	@RequestMapping(value = Routes.GIFTS_PATH, method=RequestMethod.GET)
    public @ResponseBody Collection<Gift> list(
           @RequestParam(value = Routes.TITLE_PARAMETER, required = false) String title)
    {
        if (title == null) {
            return Lists.newArrayList(gifts.findAll());
        } else {
            return Lists.newArrayList(gifts.findByTitle(title));
        }
    }

	@PreAuthorize("hasRole(mobile)")
	@RequestMapping(value = Routes.GIFTS_PATH, method = RequestMethod.POST)
	public @ResponseBody Gift create(
           @RequestBody Gift gift,
           Principal p)
    {
//        User u = new User();
//        u.setNickname(p.getName());
//        gift.setUser(u);
		gifts.save(gift);
		return gift;
	}
//
//	@PreAuthorize("hasRole(mobile)")
//	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH + "/{id}", method = RequestMethod.GET)
//	public @ResponseBody Video getVideo(@PathVariable("id") long id){
//    	Video v = gifts.findOne(id);
//
//    	if( v == null) {
//    		throw new ResourceNotFoundException("Not found");
//    	}
//
//    	return v;
//	}
//
//	@PreAuthorize("hasRole(mobile)")
//	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH + "/{id}/like", method = RequestMethod.POST)
//	public void like(@PathVariable("id") long id, Principal p, HttpServletResponse response) {
//    	Video v = gifts.findOne(id);
//
//    	if( v == null) {
//    		response.setStatus(404);
//    		return;
//    	}
//
//    	String username = p.getTitle();
//    	Set<String> likedBy = v.getLikedBy();
//    	if (likedBy.contains(username)) {
//    		response.setStatus(400);
//    		return;
//    	}
//
//    	likedBy.add(username);
//    	v.setLikes(v.getLikes() + 1);
//    	gifts.save(v);
//	}
//
//	@PreAuthorize("hasRole(mobile)")
//	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH + "/{id}/unlike", method = RequestMethod.POST)
//	public void unlike(@PathVariable("id") long id, Principal p, HttpServletResponse response) {
//    	Video v = gifts.findOne(id);
//
//    	if( v == null) {
//    		response.setStatus(404);
//    		return;
//    	}
//
//    	String username = p.getTitle();
//    	Set<String> likedBy = v.getLikedBy();
//    	if ( ! likedBy.contains(username)) {
//    		response.setStatus(400);
//    		return;
//    	}
//
//    	likedBy.remove(username);
//    	v.setLikes(v.getLikes() - 1);
//    	gifts.save(v);
//	}
//
//
//	@PreAuthorize("hasRole(mobile)")
//	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH + "/{id}/likedby", method = RequestMethod.GET)
//	public @ResponseBody Collection<String> likedBy(@PathVariable("id") long id){
//    	Video v = gifts.findOne(id);
//
//    	if( v == null) {
//    		throw new ResourceNotFoundException("Not found");
//    	}
//
//    	return v.getLikedBy();
//	}
//
//	@PreAuthorize("hasRole(mobile)")
//	@RequestMapping(value = VideoSvcApi.VIDEO_TITLE_SEARCH_PATH, method = RequestMethod.GET)
//	public @ResponseBody Collection<Video> findByTitle(@RequestParam(VideoSvcApi.TITLE_PARAMETER) String title){
//		return gifts.findByName(title);
//	}
//
//	@PreAuthorize("hasRole(mobile)")
//	@RequestMapping(value = VideoSvcApi.VIDEO_DURATION_SEARCH_PATH, method = RequestMethod.GET)
//	public @ResponseBody Collection<Video> findByDuration(@RequestParam(VideoSvcApi.DURATION_PARAMETER) long duration){
//		return gifts.findByDurationLessThan(duration);
//	}

//    @RequestMapping(value = "/test", method = RequestMethod.GET)
//    public @ResponseBody String test(){
//        return "This is a test";
//    }

}
