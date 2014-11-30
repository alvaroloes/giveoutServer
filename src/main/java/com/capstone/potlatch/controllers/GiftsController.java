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
import com.capstone.potlatch.util.ImageFileManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.util.List;
import java.util.Set;

@Controller
public class GiftsController {
    private static final String GIFT_ROOT_PATH = StringUtils.stripStart(Routes.GIFTS_PATH, "/");

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
           @RequestParam(value = Routes.LIMIT_PARAMETER, required = false, defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit,
           @RequestParam(value = Routes.NOT_FLAGGED_BY_USER_ID_PARAMETER, required = false) Long notFlaggedByUserId)
    {
        PageRequest pageRequest = new PageRequest(page, limit, new Sort(Sort.Direction.DESC, "createdAt"));
        Page<Gift> giftsPage;
        if (title == null) {
            giftsPage = notFlaggedByUserId == null ? gifts.findByGiftChainIsNotNull(pageRequest)
                                                   : gifts.findByGiftChainIsNotNullAndUserNotFlagAsInappropriate(notFlaggedByUserId, pageRequest);
        } else {
            giftsPage = notFlaggedByUserId == null ? gifts.findByGiftChainIsNotNullAndTitleLike("%"+title+"%", pageRequest)
                                                   : gifts.findByGiftChainIsNotNullAndTitleLikeAndUserNotFlagAsInappropriate("%"+title+"%", notFlaggedByUserId, pageRequest);
        }

        List<Gift> giftList = Lists.newArrayList(giftsPage);
        for(Gift gift : giftList) {
            gift.allowAccessToGiftChain = true;
        }
        return giftList;
    }

    @PreAuthorize("hasRole(mobile)")
	@RequestMapping(value = Routes.MY_GIFTS_PATH, method=RequestMethod.GET)
    public @ResponseBody Collection<Gift> listMine(
           @RequestParam(value = Routes.TITLE_PARAMETER, required = false) String title,
           @RequestParam(value = Routes.PAGE_PARAMETER, required = false, defaultValue = "0") int page,
           @RequestParam(value = Routes.LIMIT_PARAMETER, required = false, defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit,
           Principal p)
    {
        User u = users.findByUsername(p.getName());
        PageRequest pageRequest = new PageRequest(page, limit, new Sort(Sort.Direction.DESC, "createdAt"));

        List<Gift> giftList;
        if (title == null) {
            giftList =  Lists.newArrayList(gifts.findByUserId(u.getId(), pageRequest));
        } else {
            giftList =  Lists.newArrayList(gifts.findByUserIdAndTitleLike(u.getId(), "%" + title + "%", pageRequest));
        }
        for(Gift gift : giftList) {
            gift.allowAccessToGiftChain = true;
        }
        return giftList;
    }

	@PreAuthorize("hasRole(mobile)")
	@RequestMapping(value = Routes.GIFTS_PATH, method = RequestMethod.POST)
	public @ResponseBody Gift create(
           @RequestParam("gift") String giftString,
           @RequestParam MultipartFile image,
           Principal p) throws IOException {
        // In order to send in one request the gift data and the image, the gift data must be
        // sent as a json string encoded along with the image.
        // Here we parse it into a Gift object.
        Gift gift = new ObjectMapper().readValue(UriEncoder.decode(giftString), Gift.class);

        User u = users.findByUsername(p.getName());
        gift.setUser(u);
        gift.allowAccessToGiftChain = true;

        GiftChain giftChain = gift.getGiftChain();
        if (giftChain != null) {
            if (giftChain.getId() <= 0) {
                giftChains.save(giftChain);
            }
            gift.setGiftChain(giftChains.findOne(giftChain.getId()));
        }

        gifts.save(gift); // This generates the id, needed for generating the images.

        saveImages(gift, image);
		return gift;
	}

    // Spring doesn't like PUT method with request body parameters so we use a different url for updating gifts
    @PreAuthorize("hasRole(mobile)")
	@RequestMapping(value = Routes.GIFTS_UPDATE_PATH, method = RequestMethod.POST)
	public @ResponseBody Gift update(
           @PathVariable("id") long id,
           @RequestParam("gift") String giftString,
           Principal p,
           HttpServletResponse response) throws IOException {

        Gift oldGift = gifts.findOne(id);
        if( oldGift == null) {
            response.sendError(HttpStatus.NOT_FOUND.value());
            return null;
        }

        oldGift.allowAccessToGiftChain = true;
        GiftChain oldGiftChain = oldGift.getGiftChain();

        User currentUser = users.findByUsername(p.getName());
        if (oldGift.getUser().getId() != currentUser.getId()) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "You are not the owner of this gift");
            return null;
        }

        Gift gift = new ObjectMapper().readValue(UriEncoder.decode(giftString), Gift.class);

        gift.setUser(currentUser);
        gift.setId(id);
        gift.allowAccessToGiftChain = true;

        GiftChain giftChain = gift.getGiftChain();
        if (giftChain != null) {
            // The gift chain provided needs to be created?
            if (giftChain.getId() <= 0) {
                giftChains.save(giftChain);
            }
            else {
                GiftChain exisingGiftChain = giftChains.findOne(giftChain.getId());
                if (exisingGiftChain == null) {
                    response.sendError(HttpStatus.NOT_FOUND.value(), "The Gift chain provided does not exists");
                    return null;
                }

                gift.setGiftChain(exisingGiftChain);
            }

        }

        gift.setImageUrlSmall(oldGift.getImageUrlSmall());
        gift.setImageUrlMedium(oldGift.getImageUrlMedium());
        gift.setImageUrlFull(oldGift.getImageUrlFull());
		gifts.save(gift);

        long giftsRemaining = gifts.countByGiftChain(oldGiftChain);

        if (giftsRemaining == 0) {
            giftChains.delete(oldGiftChain);
        }

		return gift;
	}

    @PreAuthorize("hasRole(mobile)")
    @RequestMapping(value = Routes.GIFTS_UPDATE_IMAGE_PATH, method = RequestMethod.POST)
    public @ResponseBody Gift updateImages(
            @PathVariable("id") long id,
            @RequestParam("image") MultipartFile image,
            Principal p,
            HttpServletResponse response) throws IOException {
        Gift gift = gifts.findOne(id);
        if( gift == null) {
            response.sendError(HttpStatus.NOT_FOUND.value());
            return null;
        }

        gift.allowAccessToGiftChain = true;

        User currentUser = users.findByUsername(p.getName());
        if (gift.getUser().getId() != currentUser.getId()) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "You are not the owner of this gift");
            return null;
        }

        saveImages(gift, image);
        return gift;
    }

    private void saveImages(Gift gift, MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) return;

        BufferedImage img = ImageIO.read(image.getInputStream());
        ImageFileManager imgMan = ImageFileManager.get(GIFT_ROOT_PATH);

        imgMan.saveImage(gift.getId(), Gift.SIZE_FULL, img);
        gift.setImageUrlFull(Routes.GIFTS_IMAGE_PATH.replace("{id}", String.valueOf(gift.getId())).replace("{size}", Gift.SIZE_FULL));

        BufferedImage scaledImg = Scalr.resize(img, 640);
        imgMan.saveImage(gift.getId(), Gift.SIZE_MEDIUM, scaledImg);
        gift.setImageUrlMedium(Routes.GIFTS_IMAGE_PATH.replace("{id}", String.valueOf(gift.getId())).replace("{size}", Gift.SIZE_MEDIUM));

        scaledImg = Scalr.resize(img, 320);
        imgMan.saveImage(gift.getId(), Gift.SIZE_SMALL, scaledImg);
        gift.setImageUrlSmall(Routes.GIFTS_IMAGE_PATH.replace("{id}", String.valueOf(gift.getId())).replace("{size}", Gift.SIZE_SMALL));

        gifts.save(gift);
    }

    @RequestMapping(value = Routes.GIFTS_IMAGE_PATH, method = RequestMethod.GET)
    public @ResponseBody void getImage(
            @PathVariable("id") long id,
            @PathVariable("size") String size,
            HttpServletResponse response) throws IOException {
        Gift gift = gifts.findOne(id);
        if( gift == null) {
            response.sendError(HttpStatus.NOT_FOUND.value());
            return;
        }

        try {
            ImageFileManager.get(GIFT_ROOT_PATH).copyImage(gift.getId(), size, response.getOutputStream());
        } catch (FileNotFoundException e) {
            response.sendError(HttpStatus.NOT_FOUND.value());
        }
    }


    @PreAuthorize("hasRole(mobile)")
    @RequestMapping(value = Routes.GIFTS_ID_PATH, method = RequestMethod.DELETE)
    public @ResponseBody void delete(
            @PathVariable("id") long id,
            Principal p,
            HttpServletResponse response) throws IOException {

        Gift gift = gifts.findOne(id);
        if( gift == null) {
            response.sendError(HttpStatus.NOT_FOUND.value());
            return;
        }

        User currentUser = users.findByUsername(p.getName());
        if (gift.getUser().getId() != currentUser.getId()) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "You are not the owner of this gift");
            return;
        }

        gifts.delete(gift);

        long giftsRemaining = gifts.countByGiftChain(gift.getGiftChain());

        if (giftsRemaining == 0) {
            giftChains.delete(gift.getGiftChain());
        }
    }

	@PreAuthorize("hasRole(mobile)")
	@RequestMapping(value = Routes.GIFTS_TOUCH_PATH, method = RequestMethod.PUT)
	public @ResponseBody Gift touchOrUntouch(
           @PathVariable("id") long id,
           @RequestParam(value = Routes.REGRET_PARAMETER, required = false, defaultValue = "false") boolean regret,
           Principal p,
           HttpServletResponse response)
    {
        Gift gift = gifts.findOne(id);
        if( gift == null) {
            response.setStatus(404);
            return null;
        }
        gift.allowAccessToGiftChain = true;

        long userId = users.findByUsername(p.getName()).getId();
        Set<Long> touchedBy = gift.getTouchedByUserIds();

        // Touching or untouching more than once will have no effect.
        if (regret) {
            touchedBy.remove(userId);
        } else {
            touchedBy.add(userId);
        }

        gifts.save(gift);
		return gift;
	}

	@PreAuthorize("hasRole(mobile)")
	@RequestMapping(value = Routes.GIFTS_INAPPROPRIATE_PATH, method = RequestMethod.PUT)
	public @ResponseBody Gift markOrUnmarkAsInappropiate(
           @PathVariable("id") long id,
           @RequestParam(value = Routes.REGRET_PARAMETER, required = false, defaultValue = "false") boolean regret,
           Principal p,
           HttpServletResponse response)
    {
        Gift gift = gifts.findOne(id);
        if( gift == null) {
            response.setStatus(404);
            return null;
        }
        gift.allowAccessToGiftChain = true;

        long userId = users.findByUsername(p.getName()).getId();
        Set<Long> markedAsInappropriateBy = gift.getMarkedInappropriateByUserIds();

        // Mark or unmark as inappropriate more than once will have no effect.
        if (regret) {
            markedAsInappropriateBy.remove(userId);
        } else {
            markedAsInappropriateBy.add(userId);
        }

        gifts.save(gift);
		return gift;
	}
}
