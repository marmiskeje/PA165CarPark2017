Web.ViewModels.ReservationViewModel = function () {
    this.id = null;
    this.startDate = null;
    this.endDate = null;
    this.user = null;
    this.car = null;
    this.state = null;

    this.convertToEvent = function () {
        result = {};
        result.id = this.id;
        result.color = "#007bff";
        result.title = this.user.name;
        result.car = this.car;
        result.user = this.user;
        result.state = this.state;
        if(this.state === 'APPROVED'){
            result.title += ' \n APPROVED';
            result.color = "#40a040";
        }
        else if (this.state === 'DENIED'){
            result.title = ' \n DENIED';
            result.color = "#d6d6d6";
        }
        else{
            result.title += ' \n WAITING FOR APPROVE';
        }
        result.notSelectedColor = result.color;
        result.start = this.startDate;
        result.end = this.endDate;
        result.resourceId = this.car.id;
        result.source = this;
        result.isSecret = false;
        return result;
    }

    this.convertToSecretEvent = function () {
        result = {};
        result.id = this.id;
        result.color = "#949494";
        result.notSelectedColor = result.color;
        result.start = this.startDate;
        result.end = this.endDate;
        result.resourceId = this.car.id;
        result.title = 'Car is reserved!';
        result.source = this;
        result.isSecret = true;
        return result;
    }
}