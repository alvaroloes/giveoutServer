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

package org.magnum.mobilecloud.video;

import com.google.common.collect.Lists;
import org.magnum.mobilecloud.video.repository.Gift;
import org.magnum.mobilecloud.video.repository.GiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

@Controller
public class GiftsController {
    public static final String TITLE_PARAMETER = "title";

    public static final String GIFTS_PATH = "/gifts";
	
	/**
	 * You will need to create one or more Spring controllers to fulfill the
	 * requirements of the assignment. If you use this file, please rename it
	 * to something other than "AnEmptyController"
	 * 
	 * 
		 ________  ________  ________  ________          ___       ___  ___  ________  ___  __       
		|\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \     
		\ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_   
		 \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \  
		  \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \ 
		   \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
		    \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|
                                                                                                                                                                                                                                                                        
	 * 
	 */
	@Autowired
	private GiftRepository gifts;
	
//	@PreAuthorize("hasRole(mobile)")
	@RequestMapping(value = GIFTS_PATH, method=RequestMethod.GET)
    public @ResponseBody Collection<Gift>
    getGifts(
            @RequestParam(value = TITLE_PARAMETER, required = false) String title
    ) {
        if (title == null) {
            return Lists.newArrayList(gifts.findAll());
        } else {
            return Lists.newArrayList(gifts.findByTitle(title));
        }
    }
//
//	@PreAuthorize("hasRole(mobile)")
//	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.POST)
//	public @ResponseBody Video addVideo(@RequestBody Video video, Principal p) {
//		gifts.save(video);
//		return video;
//	}
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
