package com.infy.facebook_group3.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long userId;
	private Long friendId;
}
