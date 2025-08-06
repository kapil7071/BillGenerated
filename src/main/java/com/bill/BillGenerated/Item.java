package com.bill.BillGenerated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {

	    private String name;
	    private double weight;       // use Double, not double
	    private Integer quantity;    // use Integer, not int
	    private Double rate; 
	    private String weightUnit;       // "kg" or "gram"

	    // use Double, not double

	    // getters and setters
	

}
