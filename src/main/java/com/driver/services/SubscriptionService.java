package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription = new Subscription() ;
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());

        int no = subscriptionEntryDto.getNoOfScreensRequired() ;
        SubscriptionType type =  subscription.getSubscriptionType() ;
        Integer cost = null ;

        if(type.equals(SubscriptionType.BASIC)) cost = 500 + (200*no) ;
        if(type.equals(SubscriptionType.PRO))   cost = 800 + (250*no) ;
        if(type.equals(SubscriptionType.ELITE)) cost = 1000 + (350*no) ;

        subscription.setTotalAmountPaid(cost);

        subscription.setNoOfScreensSubscribed(no);
        subscription.setStartSubscriptionDate(new Date());

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get() ;
        user.setSubscription(subscription);
        userRepository.save(user) ;

        subscriptionRepository.save(subscription) ;   // needed ?
        return cost ;

//        return null;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user = userRepository.findById(userId).get() ;

        if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.BASIC)){
            int screens = user.getSubscription().getNoOfScreensSubscribed() ;
            Integer newAmount = 800 + (250 * screens) ;

            Integer oldAmount = user.getSubscription().getTotalAmountPaid(); ;

            user.getSubscription().setTotalAmountPaid(newAmount);
            userRepository.save(user) ;

            return newAmount - oldAmount ;
        }

        if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.PRO)){
            int screens = user.getSubscription().getNoOfScreensSubscribed() ;
            Integer newAmount = 1000 + (350 * screens) ;

            Integer oldAmount = user.getSubscription().getTotalAmountPaid(); ;

            user.getSubscription().setTotalAmountPaid(newAmount);
            userRepository.save(user) ;

            return newAmount - oldAmount ;
        }


        throw new Exception("Already the best Subscription") ;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscription = subscriptionRepository.findAll() ;
        Integer TotalRevenueOfHotstar = 0 ;

        for(Subscription i : subscription){
            TotalRevenueOfHotstar += i.getTotalAmountPaid() ;
        }
        return TotalRevenueOfHotstar ;
    }

}
