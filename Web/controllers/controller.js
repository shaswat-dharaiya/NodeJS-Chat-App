var body_parser = require('body-parser');

module.exports = function(app){
    app.get('/',function(req,res){        
        res.render('index');
    });
};