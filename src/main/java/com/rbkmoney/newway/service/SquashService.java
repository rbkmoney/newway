package com.rbkmoney.newway.service;

import com.rbkmoney.newway.model.Wrapper;

import java.util.*;
import java.util.stream.Collectors;

public abstract class SquashService<W extends Wrapper> {

    public List<W> squash(List<W> wrappers, List<Long> ids) {
        var groupedMap = wrappers.stream().collect(Collectors.groupingBy(Wrapper::getKey, LinkedHashMap::new, Collectors.toList()));
        List<W> result = new ArrayList<>();
        Iterator<Long> iterator = ids.iterator();
        groupedMap.forEach((key, pwList) -> {
            setIds(iterator, pwList);
            squashById(result, pwList);
            long count = result.stream().filter(w -> !w.isShouldInsert()).count();
            if (count > 1) {
                throw new IllegalStateException("Must be less or equal than one update statements");
            }
        });
        return result;
    }

    private void squashById(List<W> result, List<W> pwList) {
        var groupedById = pwList.stream().collect(Collectors.groupingBy(this::getId, LinkedHashMap::new, Collectors.toList()));
        groupedById.forEach((id, ws) -> {
            boolean shouldInsert = ws.get(0).isShouldInsert();
            W pwLast = ws.get(ws.size() - 1);
            pwLast.setShouldInsert(shouldInsert);
            result.add(pwLast);
        });
    }

    private void setIds(Iterator<Long> iterator, List<W> pwList) {
        W wInsert = null;
        W wUpdate = null;
        for (int i = 0; i < pwList.size(); ++i) {
            W w = pwList.get(i);
            Long id;
            if (w.isShouldInsert()) {
                wInsert = w;
                wUpdate = null;
                id = iterator.next();
            } else {
                if (wInsert != null) {
                    id = getId(wInsert);
                } else {
                    if (wUpdate != null) {
                        id = getId(wUpdate);
                    } else {
                        wUpdate = w;
                        id = iterator.next();
                    }
                }
            }
            setId(w, id);
        }
    }

    protected abstract void setId(W w, Long id);

    protected abstract Long getId(W w);

}
