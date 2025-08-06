
package com.bill.BillGenerated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Bill {

	private String orderNo;
    private String orderType;
    private LocalDateTime dateTime;
    private String customerName;
    private String customerMobile;
    private List<Item> items;
    private int totalItems;
    private int grandTotal;
    
}
