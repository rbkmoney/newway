CREATE TYPE nw.empty_cvv_bank_card AS ENUM('visa', 'mastercard', 'visaelectron', 'maestro',
                                           'forbrugsforeningen', 'dankort', 'amex', 'dinersclub',
                                           'discover', 'unionpay', 'jcb', 'nspkmir');