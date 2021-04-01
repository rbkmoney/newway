package com.rbkmoney.newway.handler.event.stock.impl.recurrent.payment.tool;

import com.rbkmoney.damsel.payment_processing.RecurrentPaymentToolChange;
import com.rbkmoney.machinegun.eventsink.MachineEvent;
import com.rbkmoney.newway.handler.event.stock.Handler;

public interface RecurrentPaymentToolHandler extends Handler<RecurrentPaymentToolChange, MachineEvent> {

}
