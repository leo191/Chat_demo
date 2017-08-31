var mongoose = require('mongoose');


var UserGroupSchema = mongoose.Schema({

users:{

       first_name: String,
       last_name: String,
       created: {
                type:Date,
                default: Date.Now
            }

        },
user_group: {
        user_id: String,
        group_id: String,
        created :{
                   type:Date,
                   default: Date.Now
                 }

}



group: {
    name: String,
    current_date:{
                    type: Date,
                    default: Date.Now
                    },
    description: String
}

});