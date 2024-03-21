import React from "react";
import Table from "../../commons/tables/table";

const columns = [
    {
        Header: 'Name',
        accessor: 'name',
    },
    {
        Header: 'Address',
        accessor: 'address',
    },
    {
        Header: 'Age',
        accessor: 'age',
    },
    {
        Header: 'Description',
        accessor: 'description',
    },
    {
        Header: 'Role',
        accessor: 'role',
    },
];

const filters = [
    {
        accessor: 'name',
    },
    {
        accessor: 'address',
    },
    {
        accessor: 'age',
    },
    {
        accessor: 'description',
    },
    {
        accessor: 'role',
    },
];

class PersonTable extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            tableData: this.props.tableData
        };
    }

    render() {
        return (
            <Table
                data={this.state.tableData}
                columns={columns}
                search={filters}
                pageSize={5}
            />
        )
    }
}

export default PersonTable;
