package com.infy.facebook_group3.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="blocked_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockedUsers {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long blockedId;
	
	@ManyToOne
	@JoinColumn(name="user_id",nullable = false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name="blocked_user_id",nullable = false)
	private User blockedUser;
	
	private LocalDateTime createdAt;
}
