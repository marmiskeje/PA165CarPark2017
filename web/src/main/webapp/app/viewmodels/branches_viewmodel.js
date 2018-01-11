Web.ViewModels.BranchesViewModel = function () {
    this.id = null;
    this.name = null;
    this.manager = null;
    this.cars = [];
    this.employees = [];

    this.convertToEvent = function () {
        result = {};
        result.id = this.id;
        result.color = "#007bff";
        result.notSelectedColor = result.color;
        result.title = this.regionalBranch.name;
        result.source = this;
        result.cars = this.cars;
        result.employees = this.employees;
        result.manager = this.employees[0];
        return result;
    };
};