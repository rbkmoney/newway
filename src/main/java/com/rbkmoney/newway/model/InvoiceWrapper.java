package com.rbkmoney.newway.model;

import com.rbkmoney.newway.domain.tables.pojos.Invoice;
import com.rbkmoney.newway.domain.tables.pojos.InvoiceCart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceWrapper extends Wrapper {
    private Invoice invoice;
    private List<InvoiceCart> carts;

    public InvoiceWrapper copy() {
        Invoice invoiceTarget = new Invoice();
        BeanUtils.copyProperties(invoice, invoiceTarget);
        InvoiceWrapper invoiceWrapperTarget = new InvoiceWrapper();
        invoiceWrapperTarget.setKey(InvoicingKey.buildKey(this));
        invoiceWrapperTarget.setInvoice(invoiceTarget);
        if (carts != null) {
            List<InvoiceCart> cartsTarget = new ArrayList<>();
            carts.forEach(c -> {
                InvoiceCart cTarget = new InvoiceCart();
                BeanUtils.copyProperties(c, cTarget);
                cartsTarget.add(cTarget);
            });
            invoiceWrapperTarget.setCarts(cartsTarget);
        }
        return invoiceWrapperTarget;
    }
}
