Web.ViewModels.CarViewModel = function () {
    this.id = null;
    this.name = null;
    this.regionalBranch = null;

    this.convertToResource = function () {
        result = {};
        result.id = this.id;
        result.title = this.name;
        result.source = this;
        return result;
    };
};