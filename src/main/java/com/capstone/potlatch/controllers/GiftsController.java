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

import com.capstone.potlatch.Constants;
import com.capstone.potlatch.Routes;
import com.capstone.potlatch.models.*;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;

@Controller
public class GiftsController {

	@Autowired
	private GiftRepository gifts;
	@Autowired
	private GiftChainRepository giftChains;
    @Autowired
    private UserRepository users;

	@RequestMapping(value = Routes.GIFTS_PATH, method=RequestMethod.GET)
    public @ResponseBody Collection<Gift> list(
           @RequestParam(value = Routes.TITLE_PARAMETER, required = false) String title,
           @RequestParam(value = Routes.PAGE_PARAMETER, required = false, defaultValue = "0") int page,
           @RequestParam(value = Routes.LIMIT_PARAMETER, required = false, defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit)
    {
        PageRequest pageRequest = new PageRequest(page, limit);
        if (title == null) {
            return Lists.newArrayList(gifts.findAll(pageRequest));
        } else {
            return Lists.newArrayList(gifts.findByTitleLike("%"+title+"%", pageRequest));
        }
    }

	@PreAuthorize("hasRole(mobile)")
	@RequestMapping(value = Routes.GIFTS_PATH, method = RequestMethod.POST)
	public @ResponseBody Gift create(
           @RequestBody Gift gift,
           Principal p)
    {
        User u = users.findByUsername(p.getName());
        gift.setUser(u);

        GiftChain giftChain = gift.getGiftChain();
        if (giftChain != null) {
            if (giftChain.getId() <= 0) {
                giftChains.save(giftChain);
            }
            gift.setGiftChain(giftChains.findOne(giftChain.getId()));
        }

		gifts.save(gift);
		return gift;
	}
}
