import { render, screen, fireEvent } from "@testing-library/react";
import CustomSelect from "../CustomSelect";

// Мок Modal
jest.mock("../Modal", () => ({
  __esModule: true,
  default: ({ isOpen, children, title }: any) =>
    isOpen ? (
      <div data-testid="modal" role="dialog" aria-label={title}>
        {children}
      </div>
    ) : null,
}));

describe("CustomSelect", () => {
  const options = [
    { value: "a", label: "Вариант A" },
    { value: "b", label: "Вариант B" },
    { value: "c", label: "Вариант C" },
  ];

  it("отображает label и выбранное значение", () => {
    render(
      <CustomSelect
        label="Выбор"
        value="b"
        options={options}
        onChange={() => {}}
      />
    );

    expect(screen.getByText("Выбор")).toBeInTheDocument();
    expect(screen.getByText("Вариант B")).toBeInTheDocument();
  });

  it("открывает модалку при клике", () => {
    render(
      <CustomSelect
        label="Выбор"
        value="a"
        options={options}
        onChange={() => {}}
      />
    );

    fireEvent.click(screen.getByText("Вариант A"));
    expect(screen.getByRole("dialog")).toBeInTheDocument();
    expect(screen.getByText("Вариант C")).toBeInTheDocument();
  });

  it("вызывает onChange при выборе опции", () => {
    const onChange = jest.fn();

    render(
      <CustomSelect
        label="Выбор"
        value="a"
        options={options}
        onChange={onChange}
      />
    );

    fireEvent.click(screen.getByText("Вариант A")); // открыть
    fireEvent.click(screen.getByText("Вариант C")); // выбрать

    expect(onChange).toHaveBeenCalledWith("c");
  });

  it("показывает 'Выберите...' если value не найден", () => {
    render(
      <CustomSelect
        label="Выбор"
        value={"unknown" as any}
        options={options}
        onChange={() => {}}
      />
    );

    expect(screen.getByText("Выберите...")).toBeInTheDocument();
  });
});
