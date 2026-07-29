package com.vanillacakes.cakes;

import com.vanillacakes.PagedResult;
import com.vanillacakes.transactions.TransactionManager;

public class CakeService {

    private final TransactionManager transactionManager;

    public CakeService(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Cake findById(Long id) {
        return transactionManager.execute(connection -> {
           CakeRepository repository = new CakeRepository(connection);
           return repository.findById(id);
        });
    }

    public Cake save(Cake cake) {
        return transactionManager.execute(connection -> {
           CakeRepository repository = new CakeRepository(connection);
           return repository.save(cake);
        });
    }

    public PagedResult<Cake> findCakes(int pageNumber, int pageSize) {
        return transactionManager.execute(connection -> {
            CakeRepository repository = new CakeRepository(connection);
            return repository.findCakes(pageNumber, pageSize);
        });
    }
}
