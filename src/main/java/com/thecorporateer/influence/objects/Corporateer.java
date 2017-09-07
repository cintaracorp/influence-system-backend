package com.thecorporateer.influence.objects;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.thecorporateer.influence.controllers.Views;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Zollak
 *
 *         Entity to store corporateers
 *
 */
@Getter
@Setter
@NoArgsConstructor

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Corporateer extends JpaEntity {

	@NotNull
	@NotBlank
	@JsonView(Views.Public.class)
	private String name;
	@OneToOne(mappedBy = "corporateer")
	@JsonIgnore
	private User user;
	@NotNull
	@Min(0)
	@JsonView(Views.Private.class)
	private int tributes = 0;
	@NotNull
	@Min(0)
	@JsonView(Views.Public.class)
	private int totalInfluence = 0;
	@NotNull
	@Min(0)
	@JsonView(Views.Public.class)
	private int lifetimeInfluence = 0;
	@OneToMany(mappedBy = "corporateer")
	private List<Influence> influence;
	@NotNull
	@ManyToOne
	@JsonView(Views.Public.class)
	private Division mainDivision;
	@NotNull
	@ManyToOne
	@JsonView(Views.Public.class)
	private Rank rank;
	@OneToMany(mappedBy = "sender")
	private List<Transaction> sentTransactions;
	@OneToMany(mappedBy = "receiver")
	private List<Transaction> receivedTransactions;
	// private Position position;

}
