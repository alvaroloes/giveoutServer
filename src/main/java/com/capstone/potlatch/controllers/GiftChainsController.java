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
import com.capstone.potlatch.models.GiftChain;
import com.capstone.potlatch.models.GiftChainRepository;
import com.capstone.potlatch.models.GiftRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;

@Controller
public class GiftChainsController {

	@Autowired
	private GiftChainRepository giftChains;
	@Autowired
	private GiftRepository gifts;

	@RequestMapping(value = Routes.GIFTS_CHAIN_PATH, method=RequestMethod.GET)
    public @ResponseBody Collection<GiftChain> list(HttpServletResponse response) {
        List<GiftChain> giftChainList = Lists.newArrayList(giftChains.findAll());
        return giftChainList;
    }
}