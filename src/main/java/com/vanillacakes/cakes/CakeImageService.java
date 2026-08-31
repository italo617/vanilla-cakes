package com.vanillacakes.cakes;

import com.vanillacakes.transactions.TransactionManager;

public class CakeImageService {
    private final TransactionManager transactionManager;

    public CakeImageService(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public CakeImageContent findByCakeId(Long cakeId) {
        return transactionManager.execute(connection -> {
           CakeImageRepository repository = new CakeImageRepository(connection);
           return repository.findByCakeId(cakeId);
        });
    }

    public long save(CakeImage cakeImage) {
        return transactionManager.execute(connection -> {
            CakeImageRepository repository = new CakeImageRepository(connection);
            return repository.save(cakeImage);
        });
    }
}
