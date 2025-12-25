package com.infy.facebook_group3.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="friends")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(FriendId.class)
public class Friend {
	@Id
	private Long userId;
	
	@Id
	private Long friendId;
	
	private LocalDateTime createdAt = LocalDateTime.now();
}
