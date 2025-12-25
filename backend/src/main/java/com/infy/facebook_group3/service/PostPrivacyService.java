package com.infy.facebook_group3.service;

import java.util.List;

import com.infy.facebook_group3.dto.PostPrivacyDTO;
import com.infy.facebook_group3.exception.FacebookException;

public interface PostPrivacyService {
	public void addPrivacyRule(Long postId, Long userId) throws FacebookException;
	public boolean canUserViewPost(Long postId, Long viewerId) throws FacebookException;
	public List<PostPrivacyDTO> getPrivacyRules(Long postId) throws FacebookException;
	public void deletePrivacyRules(Long postId) throws FacebookException;
	public List<PostPrivacyDTO> getByUser(Long userId) throws FacebookException;
}
